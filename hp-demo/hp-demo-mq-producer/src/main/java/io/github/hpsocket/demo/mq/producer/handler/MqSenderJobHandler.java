package io.github.hpsocket.demo.mq.producer.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqSenderJobHandler
{
	@Autowired
	private DomainEventService<DemoEvent> demoEventService;
	
	@Autowired(required = false)
	@Qualifier("defaultRabbitTemplate")
	private RabbitTemplate defaultRabbitTemplate;

	@Autowired(required = false)
	@Qualifier("firstRabbitTemplate")
	private RabbitTemplate firstRabbitTemplate;

	@Autowired(required = false)
	@Qualifier("secondRabbitTemplate")
	private RabbitTemplate secondRabbitTemplate;

	@Autowired(required = false)
	@Qualifier("thirdRabbitTemplate")
	private RabbitTemplate thirdRabbitTemplate;
	
	private RabbitTemplate[] rabbitTemplates;
	
	@PostConstruct
	public void postConstruct()
	{
		rabbitTemplates = new RabbitTemplate[]
		{
			defaultRabbitTemplate, 
			firstRabbitTemplate,
			secondRabbitTemplate,
			thirdRabbitTemplate
		};
	}

	/** MQ 消息发送 JOB */
	@ExclusiveJob(jobName = "sendMqEvent", cron = "*/3 * * * * ?")
	public void sendMqEvent()
	{
		Long preId = 0L;
		final int BATCH = DomainEventService.DEFAULT_MARK_BATCH_SIZE;
		
		while(true)
		{
			List<DemoEvent> events = demoEventService.markMultiEventToSend(BATCH, preId);
			int size = events.size();
			
			if(size == 0)
				break;
			
			List<Long> succ = new ArrayList<>(size);
			List<Long> fail = new ArrayList<>(size);
			
			for(DemoEvent event : events)
			{
				Long id = event.getId();
				String msgId = event.getMsgId();
				
				try
				{
					CorrelationData corData = new CorrelationData(msgId);
					RabbitTemplate template = rabbitTemplates[event.getRegionId()];
					
					template.send(event.getExchange(), event.getRoutingKey(), event.toMessage(), corData);
					succ.add(id);
				}
				catch(Exception e)
				{
					fail.add(id);
					log.warn("send MQ message fail -> (id: {}, msgId: {}) : {}", id, msgId, e.getMessage(), e);
				}
			}
			
			demoEventService.markMultiEventSendSuccess(succ);
			demoEventService.markMultiEventSendFail(fail);
			
			if(size < BATCH)
				break;
			
			preId = events.get(size - 1).getId();
		}
	}
	
	/** MQ 消息状态重置补偿 JOB */
	@ExclusiveJob(jobName = "compensateMqEvent", cron = "15 */1 * * * ?")
	public void compensateMqEvent()
	{
		while(true)
		{
			if(!demoEventService.resetSendingEventToSendFail())
				break;
		}
	}
}
