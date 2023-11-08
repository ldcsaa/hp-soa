package io.github.hpsocket.demo.kafka.sender;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.demo.kafka.config.AppConfig;
import io.github.hpsocket.demo.kafka.entity.DemoEvent;
import io.github.hpsocket.demo.kafka.entity.Order;
import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaSender
{
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private AtomicInteger seq = new AtomicInteger(0);

    public Order sendOrder(Order order)
    {
        JSONObject headers = new JSONObject();
        headers.put("x-customer-trace-id", IdGenerator.nextIdStr());
        
        DemoEvent event = new DemoEvent(order.getId(), order.getRegionId());
        String corId = new StringBuilder(event.getMsgId()).append('#').append((seq.incrementAndGet()) % 100).toString();
        Integer regionId = event.getRegionId();
        
        event.setDomainName(AppConfig.DOMAIN_NAME);
        event.setEventName(AppConfig.CREATE_ORDER_EVENT_NAME);
        event.setTopic(AppConfig.TOPICS[regionId]);
        event.setTopicPartition(null);
        event.setMsgTimestamp(null);
        event.setMsgKey(IdGenerator.nextIdStr());
        event.setMsgHeaders(headers.toString());
        event.setMsg(JSONObject.toJSONString(order));
        
        try
        {
            long offset = -1;
            
            if(!kafkaTemplate.isTransactional() || kafkaTemplate.isAllowNonTransactional())
                offset = kafkaTemplate.send(event.toProducerRecord(corId)).get().getRecordMetadata().offset();
            else
                offset = kafkaTemplate.executeInTransaction(op -> 
                {
                    try
                    {
                        return op.send(event.toProducerRecord(corId)).get().getRecordMetadata().offset();
                    }
                    catch(InterruptedException | ExecutionException e)
                    {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });
                            
            log.info("send MQ message -> (regionId: {}, msgId: {}, offset: {})", regionId, event.getMsgId(), offset);
        }
        catch(Exception e)
        {
            log.error("send MQ message fail -> (regionId: {}, msgId: {}) : {}", regionId, event.getMsgId(), e.getMessage(), e);
        }

        return order;
    }

}
