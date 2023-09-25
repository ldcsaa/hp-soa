package io.github.hpsocket.demo.mq.producer.sender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.demo.mq.producer.config.AppConfig;
import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.demo.mq.producer.entity.Order;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StreamSender
{
    @Autowired(required = false)
    @Qualifier("defaultRabbitStreamTemplate")
    private RabbitStreamTemplate defaultRabbitStreamTemplate;

    @Autowired(required = false)
    @Qualifier("firstRabbitStreamTemplate")
    private RabbitStreamTemplate firstRabbitStreamTemplate;

    @Autowired(required = false)
    @Qualifier("secondRabbitStreamTemplate")
    private RabbitStreamTemplate secondRabbitStreamTemplate;

    @Autowired(required = false)
    @Qualifier("thirdRabbitStreamTemplate")
    private RabbitStreamTemplate thirdRabbitStreamTemplate;
    
    private RabbitStreamTemplate[] rabbitStreamTemplates;
    
    private AtomicInteger seq = new AtomicInteger(0);
    
    @PostConstruct
    public void postConstruct()
    {
        rabbitStreamTemplates = new RabbitStreamTemplate[]
        {
            defaultRabbitStreamTemplate, 
            firstRabbitStreamTemplate,
            secondRabbitStreamTemplate,
            thirdRabbitStreamTemplate
        };
    }


    public Order sendOrder(Order order)
    {
        Integer regionId = order.getRegionId();
        RabbitStreamTemplate streamTemplate = rabbitStreamTemplates[regionId];
        
        DemoEvent event = new DemoEvent(order.getId(), order.getRegionId());
        String corId = new StringBuilder(event.getMsgId()).append('#').append((seq.incrementAndGet()) % 100).toString();
        
        event.setDomainName(AppConfig.DOMAIN_NAME);
        event.setEventName(AppConfig.CREATE_ORDER_EVENT_NAME);
        event.setExchange(AppConfig.REGION_EXCHANGES[event.getRegionId()]);
        event.setRoutingKey(AppConfig.CREATE_ORDER_ROUTING_KEY);
        event.setMsg(JSONObject.toJSONString(order));
        
        try
        {
            CompletableFuture<Boolean> future = streamTemplate.send(event.toStreamMessage(corId));
            Boolean ack = future.get();
            
            log.info("send MQ message -> (regionId: {}, msgId: {}, ack: {})", regionId, event.getMsgId(), ack);
        }
        catch(Exception e)
        {
            log.error("send MQ message fail -> (regionId: {}, msgId: {}) : {}", regionId, event.getMsgId(), e.getMessage(), e);
        }

        return order;
    }

}
