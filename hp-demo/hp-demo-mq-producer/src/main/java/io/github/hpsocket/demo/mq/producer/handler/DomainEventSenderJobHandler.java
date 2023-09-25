package io.github.hpsocket.demo.mq.producer.handler;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;
import io.github.hpsocket.soa.starter.rabbitmq.producer.sender.AbstractRabbitmqDomainEventSender;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;
import jakarta.annotation.PostConstruct;

@Component
public class DomainEventSenderJobHandler extends AbstractRabbitmqDomainEventSender<DemoEvent>
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
    @Override
    @ExclusiveJob(jobName = "sendMqEvent", cron = "*/3 * * * * ?")
    public void sendMqEvent()
    {
        super.sendMqEvent();
    }
    
    /** MQ 消息状态重置补偿 JOB */
    @Override
    @ExclusiveJob(jobName = "compensateMqEvent", cron = "15 */1 * * * ?")
    public void compensateMqEvent()
    {
        super.compensateMqEvent();
    }

    @Override
    protected RabbitTemplate getRabbitTemplate(DemoEvent event)
    {
        return rabbitTemplates[event.getRegionId()];
    }

    @Override
    protected DomainEventService<DemoEvent> getDomainEventService()
    {
        return demoEventService;
    }
}
