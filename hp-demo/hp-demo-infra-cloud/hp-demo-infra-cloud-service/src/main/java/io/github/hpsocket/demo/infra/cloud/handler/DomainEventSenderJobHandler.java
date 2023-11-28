package io.github.hpsocket.demo.infra.cloud.handler;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.hpsocket.demo.infra.cloud.entity.DemoEvent;
import io.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;
import io.github.hpsocket.soa.starter.rabbitmq.producer.sender.AbstractRabbitmqDomainEventSender;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;

@Component
public class DomainEventSenderJobHandler extends AbstractRabbitmqDomainEventSender<DemoEvent>
{
    @Autowired
    private DomainEventService<DemoEvent> demoEventService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
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
        return rabbitTemplate;
    }

    @Override
    protected DomainEventService<DemoEvent> getDomainEventService()
    {
        return demoEventService;
    }
}
