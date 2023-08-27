package io.github.hpsocket.soa.starter.rabbitmq.producer.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;

import static io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent.*;

public class DomainEventServiceImpl<M extends BaseMapper<T>, T extends DomainEvent> extends ServiceImpl<M, T> implements DomainEventService<T>
{
	private static final String SQL_SET_RETRIES					= String.format("retries = IIF(send_flag = %d, 0, retries + 1)", SF_NOT_SEND);
	private static final String SQL_PATTERN_LAST_SEND_TIME_LE	= "DATE_ADD(NOW(), INTERVAL -%d SECOND)";
	
	@Override
	public T markEventToSend()
	{
		T event = lambdaQuery().in(T::getSendFlag, SF_NOT_SEND, SF_SEND_FAIL).orderByAsc(T::getId).last("LIMIT 1").one();
		
		if(event != null)
		{
			lambdaUpdate().eq(T::getId, event.getId())
			.set(T::getSendFlag, SF_SENDING)
			.set(T::getLastSendTime, LocalDateTime.now())
			.setSql(SQL_SET_RETRIES)
			.update();
		}
		
		return event;
	}

	@Override
	public List<T> markEventToSend(int count)
	{
		List<T> events = lambdaQuery().in(T::getSendFlag, SF_NOT_SEND, SF_SEND_FAIL).orderByAsc(T::getId).last("LIMIT " + count).list();
		
		if(!events.isEmpty())
		{
			lambdaUpdate().in(T::getId, events.stream().map(T::getId))
			.set(T::getSendFlag, SF_SENDING)
			.set(T::getLastSendTime, LocalDateTime.now())
			.setSql(SQL_SET_RETRIES)
			.update();
		}
		
		return events;
	}

	@Override
	public boolean markEventSendSuccess(long id)
	{
		return updateEventSendFlag(id, SF_SEND_SUCC);
	}

	@Override
	public boolean markEventSendSuccess(List<Long> ids)
	{
		return updateEventSendFlag(ids, SF_SEND_SUCC);
	}

	@Override
	public boolean markEventSendFail(long id)
	{
		return updateEventSendFlag(id, SF_SEND_FAIL);
	}

	@Override
	public boolean markEventSendFail(List<Long> ids)
	{
		return updateEventSendFlag(ids, SF_SEND_FAIL);
	}

	@Override
	public boolean resetSendingEventToSendFail()
	{
		return resetSendingEventToSendFail(DEFAULT_SEND_TIMEOUT, DEFAULT_BATCH_SIZE);
	}

	@Override
	public boolean resetSendingEventToSendFail(int sendTimeout)
	{
		return resetSendingEventToSendFail(sendTimeout, DEFAULT_BATCH_SIZE);
	}

	@Override
	public boolean resetSendingEventToSendFail(int sendTimeout, int batchSize)
	{
		List<T> events = lambdaQuery().eq(T::getSendFlag, SF_SENDING)
							.le(T::getLastSendTime, String.format(SQL_PATTERN_LAST_SEND_TIME_LE, sendTimeout))
							.orderByDesc(T::getId).last("LIMIT " + batchSize).list();
		
		if(!events.isEmpty())
		{
			return lambdaUpdate().in(T::getId, events.stream().map(T::getId))
					.set(T::getSendFlag, SF_SEND_FAIL)
					.update();
		}
		
		return true;
	}

	protected boolean updateEventSendFlag(long id, int sendFlag)
	{
		return lambdaUpdate().eq(T::getId, id)
			.set(T::getSendFlag, sendFlag)
			.update();
	}

	protected boolean updateEventSendFlag(List<Long> ids, int sendFlag)
	{
		return lambdaUpdate().in(T::getId, ids)
			.set(T::getSendFlag, sendFlag)
			.update();
	}

	protected boolean updateEventSendFlag(List<Long> ids, List<Integer> sendFlags)
	{
		boolean isOK = false;
		int size = ids.size();
		
		Assert.isTrue(size == sendFlags.size(), "size of ids not equals size of sendFlags");
		
		for(int i = 0; i < size; i++)
		{
			if(updateEventSendFlag(ids.get(i), sendFlags.get(i)) && !isOK)
				isOK = true;
		}
		
		return isOK;
	}

}
