package io.github.hpsocket.soa.starter.rabbitmq.producer.sender;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>领域事件消息发送器基类（经典消息）</b><br>
 * 使用步骤：<br>
 * <ol>
 * <li>应用程序 Bean 继承该类，并实现 {@linkplain #getRabbitTemplate(T)} 和 {@linkplain #getDomainEventService()} 抽象方法</li>
 * <li>应用程序 Bean 通过 ExclusiveJob 或 XxlJob 定时调用 {@linkplain #sendMqEvent()} 发送领域事件</li>
 * <li>应用程序 Bean 通过 ExclusiveJob 或 XxlJob 定时调用 {@linkplain #compensateMqEvent()} 补偿领域事件</li>
 * </ol>
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
abstract public class AbstractRabbitmqDomainEventSender<T extends DomainEvent>
{
	private int sendBatchSize		= DomainEventService.DEFAULT_SEND_BATCH_SIZE;
	private int compensateBatchSize	= DomainEventService.DEFAULT_COMPENSATE_BATCH_SIZE;
	private int compensateTimeout	= DomainEventService.DEFAULT_COMPENSATE_TIMEOUT;
	
	/** 获取消息发送模版 {@linkplain RabbitTemplate}
	 * @param event 待发送的领域事件
	 */
	abstract protected RabbitTemplate getRabbitTemplate(T event);
	
	/** 获取领域事件服务 {@linkplain DomainEventService} */
	abstract protected DomainEventService<T> getDomainEventService();
	
	public AbstractRabbitmqDomainEventSender(int sendBatchSize)
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
					Message message = event.toMessage(corId);
					CorrelationData corData = new CorrelationData(corId);
					RabbitTemplate template = getRabbitTemplate(event);
					
					template.send(event.getExchange(), event.getRoutingKey(), message, corData);
					Confirm confirm = corData.getFuture().get();
					
					if(!confirm.isAck())
						log.warn("send MQ message un-ack -> (id: {}, msgId: {}, corId: {}) : {}", id, msgId, corId, confirm.getReason());
					
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
