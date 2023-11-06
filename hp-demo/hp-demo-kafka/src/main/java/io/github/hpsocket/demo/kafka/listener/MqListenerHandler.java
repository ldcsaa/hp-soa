package io.github.hpsocket.demo.kafka.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.starter.kafka.util.KafkaConstant;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.demo.kafka.config.AppConfig.*;

import java.util.List;

@Slf4j
@Component
public class MqListenerHandler
{
    @KafkaListener(topics = TOPIC_0)
    public void onMessageTopic0(Message<String> msg, Consumer<String, String> consumer, Acknowledgment ack)
    {
        logMessage(msg);
    }

    @KafkaListener(topics = TOPIC_1)
    public void onMessageTopic1(List<Message<String>> msgs, Consumer<String, String> consumer, Acknowledgment ack)
    {
        msgs.forEach(msg -> logMessage(msg));
    }
    
    @KafkaListener(topics = TOPIC_2)
    public void onMessageTopic2(ConsumerRecord<String, String> msg, Consumer<String, String> consumer, Acknowledgment ack)
    {
        logMessage(msg);
    }
    
    @KafkaListener(topics = TOPIC_3)
    public void onMessageTopic3(List<ConsumerRecord<String, String>> msgs, Consumer<String, String> consumer, Acknowledgment ack)
    {
        msgs.forEach(msg -> logMessage(msg));
    }
    
    @KafkaListener(topics = TOPIC_4)
    public void onMessageTopic4(String msg, Consumer<String, String> consumer, Acknowledgment ack)
    {
        logMessage(msg);        
    }
    
    @KafkaListener(topics = TOPIC_5)
    public void onMessageTopic5(List<String> msgs, Consumer<String, String> consumer, Acknowledgment ack)
    {
        msgs.forEach(msg -> logMessage(msg));
    }

    private void logMessage(Message<String> msg)
    {
        JSONObject json = new JSONObject();
        
        MessageHeaders headers = msg.getHeaders();
        json.put("domain", headers.get(KafkaConstant.HEADER_DOMAIN_NAME));
        json.put("eventName", headers.get(KafkaConstant.HEADER_EVENT_NAME));
        json.put("topic", TOPIC_0);
        //json.put("partition", payload.get("partition"));
        //json.put("timestamp", );
        //json.put("key", );
        //json.put("headers", );
        json.put("sourceRequestId", headers.get(KafkaConstant.HEADER_EVENT_NAME));
        json.put("correlaDataId", headers.get(KafkaConstant.HEADER_CORRELA_DATA_ID));
        json.put("messageId", headers.get(KafkaConstant.HEADER_MSG_ID));
        json.put("msg", JSONObject.parseObject(msg.getPayload()));
        
        longMessage(json);
    }
    
    private void logMessage(ConsumerRecord<String, String> msg)
    {
        JSONObject json = new JSONObject();
        
        Headers headers = msg.headers();
        json.put("domain", headers.lastHeader(KafkaConstant.HEADER_DOMAIN_NAME));
        json.put("eventName", headers.lastHeader(KafkaConstant.HEADER_EVENT_NAME));
        json.put("topic", msg.topic());
        json.put("partition", msg.partition());
        json.put("timestamp", msg.timestamp());
        json.put("key", msg.key());
        json.put("headers", msg.headers());
        json.put("sourceRequestId", headers.lastHeader(KafkaConstant.HEADER_EVENT_NAME));
        json.put("correlaDataId", headers.lastHeader(KafkaConstant.HEADER_CORRELA_DATA_ID));
        json.put("messageId", headers.lastHeader(KafkaConstant.HEADER_MSG_ID));
        json.put("msg", JSONObject.parseObject(msg.value()));
        
        longMessage(json);
    }
    
    private void logMessage(String msg)
    {
        JSONObject json = new JSONObject();
        json.put("msg", JSONObject.parseObject(msg));
        
        longMessage(json);
    }
    
    private void longMessage(JSONObject json)
    {
        log.info("receive message -> {}", json);
    }

}
