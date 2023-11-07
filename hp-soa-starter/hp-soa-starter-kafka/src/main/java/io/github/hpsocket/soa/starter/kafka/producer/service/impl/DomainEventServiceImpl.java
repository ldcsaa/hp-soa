package io.github.hpsocket.soa.starter.kafka.producer.service.impl;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.starter.kafka.producer.entity.DomainEvent;
import io.github.hpsocket.soa.starter.kafka.producer.service.DomainEventService;

import static io.github.hpsocket.soa.starter.kafka.util.KafkaConstant.*;

/** <b>领域事件服务实现</b><br>
 * 继承 {@linkplain ServiceImpl} 并实现 {@linkplain DomainEventService} 接口
 */
public class DomainEventServiceImpl<M extends BaseMapper<T>, T extends DomainEvent> extends ServiceImpl<M, T> implements DomainEventService<T>
{
    private static final String SQL_SET_RETRIES                 = String.format("retries = IF(send_flag = %d, 0, retries + 1)", SF_NOT_SEND);
    private static final String SQL_PATTERN_LAST_SEND_TIME_LE   = "DATE_ADD(NOW(), INTERVAL -%d SECOND)";
    
    @Override
    public T markOneEventToSend()
    {
        return markOneEventToSend(0L);
    }
    
    @Override
    public T markOneEventToSend(Long preId)
    {
        T event = lambdaQuery()
                    .gt(T::getId, preId)
                    .in(T::getSendFlag, SF_NOT_SEND, SF_SEND_FAIL)
                    .orderByAsc(T::getId)
                    .last("LIMIT 1")
                    .one();
        
        if(event != null)
        {
            lambdaUpdate()
                .eq(T::getId, event.getId())
                .setSql(SQL_SET_RETRIES)
                .set(T::getSendFlag, SF_SENDING)
                .set(T::getLastSendTime, ZonedDateTime.now())
                .update();
        }
        
        return event;
    }

    @Override
    public List<T> markMultiEventToSend()
    {
        return markMultiEventToSend(DEFAULT_SEND_BATCH_SIZE, 0L);
    }

    @Override
    public List<T> markMultiEventToSend(Long preId)
    {
        return markMultiEventToSend(DEFAULT_SEND_BATCH_SIZE, preId);
    }

    @Override
    public List<T> markMultiEventToSend(int batchSize)
    {
        return markMultiEventToSend(batchSize, 0L);
    }

    @Override
    public List<T> markMultiEventToSend(int batchSize, Long preId)
    {
        List<T> events = lambdaQuery()
                            .gt(T::getId, preId)
                            .in(T::getSendFlag, SF_NOT_SEND, SF_SEND_FAIL)
                            .orderByAsc(T::getId)
                            .last("LIMIT " + batchSize)
                            .list();

        if(!events.isEmpty())
        {
            lambdaUpdate()
                .in(T::getId, events.stream().map(T::getId).toList())
                .setSql(SQL_SET_RETRIES)
                .set(T::getSendFlag, SF_SENDING)
                .set(T::getLastSendTime, ZonedDateTime.now())
                .update();
        }
        
        return events;
    }

    @Override
    public boolean markOneEventSendSuccess(Long id)
    {
        return updateEventSendFlag(id, SF_SEND_SUCC, SF_SENDING);
    }

    @Override
    public boolean markMultiEventSendSuccess(Collection<Long> ids)
    {
        return updateEventSendFlag(ids, SF_SEND_SUCC, SF_SENDING);
    }

    @Override
    public boolean markOneEventSendFail(Long id)
    {
        return updateEventSendFlag(id, SF_SEND_FAIL, SF_SENDING);
    }

    @Override
    public boolean markMultiEventSendFail(Collection<Long> ids)
    {
        return updateEventSendFlag(ids, SF_SEND_FAIL, SF_SENDING);
    }

    @Override
    public boolean resetSendingEventToSendFail()
    {
        return resetSendingEventToSendFail(DEFAULT_COMPENSATE_TIMEOUT, DEFAULT_COMPENSATE_BATCH_SIZE);
    }

    @Override
    public boolean resetSendingEventToSendFail(int sendTimeout)
    {
        return resetSendingEventToSendFail(sendTimeout, DEFAULT_COMPENSATE_BATCH_SIZE);
    }

    @Override
    public boolean resetSendingEventToSendFail(int sendTimeout, int batchSize)
    {
        List<T> events = lambdaQuery()
                            .eq(T::getSendFlag, SF_SENDING)
                            .leSql(T::getLastSendTime, String.format(SQL_PATTERN_LAST_SEND_TIME_LE, sendTimeout))
                            .orderByAsc(T::getId)
                            .last("LIMIT " + batchSize)
                            .list();
        
        if(events.isEmpty())
            return false;
        
        return lambdaUpdate()
                    .in(T::getId, events.stream().map(T::getId).toList())
                    .set(T::getSendFlag, SF_SEND_FAIL)
                    .update();
    }

    protected boolean updateEventSendFlag(Long id, int sendFlag, int curSendFlag)
    {
        LambdaUpdateChainWrapper<T> query = lambdaUpdate().eq(T::getId, id);
        
        if(curSendFlag >= 0)
            query.eq(T::getSendFlag, curSendFlag);
        
        return query
                .set(T::getSendFlag, sendFlag)
                .update();
    }

    protected boolean updateEventSendFlag(Collection<Long> ids, int sendFlag, int curSendFlag)
    {
        if(ids.isEmpty())
            return false;
        
        LambdaUpdateChainWrapper<T> query = lambdaUpdate().in(T::getId, ids);
        
        if(curSendFlag >= 0)
            query.eq(T::getSendFlag, curSendFlag);
        
        return query
                .set(T::getSendFlag, sendFlag)
                .update();
    }

    protected boolean updateEventSendFlag(Collection<Pair<Long, Integer>> idSendFlags)
    {
        Map<Integer, Collection<Long>> sendFlagIds = idSendFlags.stream()
                                                .collect(
                                                    Collectors.groupingBy(
                                                        Pair::getSecond,
                                                        Collectors.mapping(
                                                            Pair::getFirst,
                                                            Collectors.toCollection(HashSet::new))));
        
        return updateEventSendFlag(sendFlagIds);
    }

    protected boolean updateEventSendFlag(Map<Integer, Collection<Long>> sendFlagIds)
    {
        boolean isOK = false;
        
        for(Entry<Integer, Collection<Long>> entry : sendFlagIds.entrySet())
        {
            if(updateEventSendFlag(entry.getValue(), entry.getKey(), -1) && !isOK)
                isOK = true;
        }
        
        return isOK;
    }

}
