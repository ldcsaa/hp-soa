package io.github.hpsocket.soa.starter.rabbitmq.producer.sender;

import java.util.ArrayList;
import java.util.List;

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

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
abstract public class AbstractRabbitmqSender<T extends DomainEvent>
{
	private int sendBatchSize		= DomainEventService.DEFAULT_SEND_BATCH_SIZE;
	private int compensateBatchSize	= DomainEventService.DEFAULT_RESET_BATCH_SIZE;
	private int compensateTimeout	= DomainEventService.DEFAULT_RESET_TIMEOUT;
	
	abstract protected RabbitTemplate getRabbitTemplate(T event);
	abstract protected DomainEventService<T> getDomainEventService();
	
	public AbstractRabbitmqSender(int sendBatchSize)
	{
		this.sendBatchSize = sendBatchSize;
	}

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
					CorrelationData corData = new CorrelationData(corId);
					RabbitTemplate template = getRabbitTemplate(event);
					
					template.send(event.getExchange(), event.getRoutingKey(), event.toMessage(), corData);
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
