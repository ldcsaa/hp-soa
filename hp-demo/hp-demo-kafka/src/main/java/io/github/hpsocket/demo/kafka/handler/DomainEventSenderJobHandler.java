package io.github.hpsocket.demo.kafka.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.github.hpsocket.demo.kafka.entity.DemoEvent;
import io.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;
import io.github.hpsocket.soa.starter.kafka.producer.sender.AbstractKafkaDomainEventSender;
import io.github.hpsocket.soa.starter.kafka.producer.service.DomainEventService;

@Component
public class DomainEventSenderJobHandler extends AbstractKafkaDomainEventSender<DemoEvent>
{
    @Autowired
    private DomainEventService<DemoEvent> demoEventService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
    protected KafkaTemplate<String, String> getKafkaTemplate(DemoEvent event)
    {
        return kafkaTemplate;
    }

    @Override
    protected DomainEventService<DemoEvent> getDomainEventService()
    {
        return demoEventService;
    }
}
