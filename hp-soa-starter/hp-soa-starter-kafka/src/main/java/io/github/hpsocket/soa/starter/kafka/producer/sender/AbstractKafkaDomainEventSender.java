package io.github.hpsocket.soa.starter.kafka.producer.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

import io.github.hpsocket.soa.starter.kafka.producer.entity.DomainEvent;
import io.github.hpsocket.soa.starter.kafka.producer.service.DomainEventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>领域事件消息发送器基类（经典消息）</b><br>
 * 使用步骤：<br>
 * <ol>
 * <li>应用程序 Bean 继承该类，并实现 {@linkplain #getKafkaTemplate(T)} 和 {@linkplain #getDomainEventService()} 抽象方法</li>
 * <li>应用程序 Bean 通过 ExclusiveJob 或 XxlJob 定时调用 {@linkplain #sendMqEvent()} 发送领域事件</li>
 * <li>应用程序 Bean 通过 ExclusiveJob 或 XxlJob 定时调用 {@linkplain #compensateMqEvent()} 补偿领域事件</li>
 * </ol>
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
abstract public class AbstractKafkaDomainEventSender<T extends DomainEvent>
{
    private int sendBatchSize       = DomainEventService.DEFAULT_SEND_BATCH_SIZE;
    private int compensateBatchSize = DomainEventService.DEFAULT_COMPENSATE_BATCH_SIZE;
    private int compensateTimeout   = DomainEventService.DEFAULT_COMPENSATE_TIMEOUT;
    
    /** 获取消息发送模版 {@linkplain KafkaTemplate}
     * @param event 待发送的领域事件
     */
    abstract protected KafkaTemplate<String, String> getKafkaTemplate(T event);
    
    /** 获取领域事件服务 {@linkplain DomainEventService} */
    abstract protected DomainEventService<T> getDomainEventService();
    
    public AbstractKafkaDomainEventSender(int sendBatchSize)
    {
        this.sendBatchSize = sendBatchSize;
    }

    /** （分批）发送领域事件 */
    public void sendMqEvent()
    {
        Long preId = 0L;
        DomainEventService<T> domainEventService = getDomainEventService();
        
        while(true)
        {
            List<T> events = domainEventService.markMultiEventToSend(sendBatchSize, preId);
            int size = events.size();
            
            if(size == 0)
                break;
            
            List<Long> succ = new ArrayList<>(size);
            List<Long> fail = new ArrayList<>(size);
            
            for(T event : events)
            {
                Long id = event.getId();
                String msgId = event.getMsgId();
                String corId = new StringBuilder(msgId).append('#').append(event.getRetries()).toString();
                
                try
                {
                    ProducerRecord<String, String> rc = event.toProducerRecord(corId);
                    KafkaTemplate<String, String> template = getKafkaTemplate(event);
                    
                    if(!template.isTransactional() || template.isAllowNonTransactional())
                        template.send(rc).get();
                    else
                        template.executeInTransaction(op -> 
                        {
                            try
                            {
                                return op.send(rc).get();
                            }
                            catch(InterruptedException | ExecutionException e)
                            {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        });
                    
                    succ.add(id);
                }
                catch(Exception e)
                {
                    fail.add(id);
                    log.warn("send MQ message fail -> (id: {}, msgId: {}, corId: {}) : {}", id, msgId, corId, e.getMessage(), e);
                }
            }
            
            if(!succ.isEmpty())
                domainEventService.markMultiEventSendSuccess(succ);
            if(!fail.isEmpty())
                domainEventService.markMultiEventSendFail(fail);
            
            if(size < sendBatchSize)
                break;
            
            preId = events.get(size - 1).getId();
        }
    }
    
    /** （分批）补偿领域事件 */
    public void compensateMqEvent()
    {
        DomainEventService<T> domainEventService = getDomainEventService();

        while(true)
        {
            if(!domainEventService.resetSendingEventToSendFail(compensateTimeout, compensateBatchSize))
                break;
        }
    }

}
