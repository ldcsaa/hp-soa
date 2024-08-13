package io.github.hpsocket.demo.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.starter.kafka.util.KafkaConstant;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.demo.kafka.config.AppConfig.*;

import java.util.List;

@Slf4j
@Component
public class MqListenerHandler
{
    //////////////////////////////////////////////////
    // 单条消费配置：spring.kafka.listener.type=single //
    //////////////////////////////////////////////////
    
    /** 单条消费 */
    @KafkaListener(topics = TOPIC_0, groupId = "GROUP_SINGLE", batch = "false", autoStartup = "false")
    public void onSingleMessage0(Message<String> msg/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        logMessage(msg);
    }

    /** 单条消费 */
    @KafkaListener(topics = TOPIC_1, groupId = "GROUP_SINGLE", batch = "false", autoStartup = "false")
    public void onSingleMessage1(ConsumerRecord<String, String> msg/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        logMessage(msg);
    }
    
    /** 单条消费 */
    @KafkaListener(topics = TOPIC_2, groupId = "GROUP_SINGLE", batch = "false", autoStartup = "false")
    public void onSingleMessage2(String msg/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        logMessage(msg);        
    }
    
    //////////////////////////////////////////////////
    // 批量消费配置：spring.kafka.listener.type=batch ///
    //////////////////////////////////////////////////
        
    /** 批量消费 */
    @KafkaListener(topics = TOPIC_0, groupId = "GROUP_BATCH", batch = "true", autoStartup = "false")
    public void onBatchMessage0(List<Message<String>> msgs/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        msgs.forEach(msg -> logMessage(msg));
    }
    
    /** 批量消费 */
    @KafkaListener(topics = TOPIC_1, groupId = "GROUP_BATCH", batch = "true", autoStartup = "false")
    public void onBatchMessage1(ConsumerRecords<String, String> msgs/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        msgs.forEach(msg -> logMessage(msg));
    }
    
    /** 批量消费 */
    @KafkaListener(topics = TOPIC_2, groupId = "GROUP_BATCH", batch = "true", autoStartup = "false")
    public void onBatchMessage2(List<String> msgs/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        msgs.forEach(msg -> logMessage(msg));
    }

    //////////////////////////////////////////////////
    // 纯文本消息                                    ///
    //////////////////////////////////////////////////
    
    /** 单条消费 */
    @KafkaListener(topics = TOPIC_TEXT, groupId = "GROUP_SINGLE", batch = "false", autoStartup = "false")
    public void onSingleTextMessage0(Message<String> msg/*, Acknowledgment ack, Consumer<String, String> consumer*/)
    {
        logTextMessage(msg);
    }

    private void logMessage(Message<String> msg)
    {
        JSONObject json = new JSONObject();
        MessageHeaders headers = msg.getHeaders();
        
        json.put("topic", headers.get(KafkaHeaders.RECEIVED_TOPIC));
        json.put("partition", headers.get(KafkaHeaders.RECEIVED_PARTITION));
        json.put("timestampType", headers.get(KafkaHeaders.TIMESTAMP_TYPE));
        json.put("timestamp", headers.get(KafkaHeaders.RECEIVED_TIMESTAMP));
        json.put("groupId", headers.get(KafkaHeaders.GROUP_ID));
        json.put("key", headers.get(KafkaHeaders.RECEIVED_KEY));
        
        json.put("messageId", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_MSG_ID)));
        json.put("sourceRequestId", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_SOURCE_REQUEST_ID)));
        json.put("domainName", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_DOMAIN_NAME)));
        json.put("eventName", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_EVENT_NAME)));
        json.put("correlationId", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_CORRELATION_ID)));                    

        json.put("msg", JSONObject.parseObject(msg.getPayload()));
        
        logMessage(json);
    }
    
    private void logMessage(ConsumerRecord<String, String> msg)
    {
        JSONObject json = new JSONObject();
        Headers headers = msg.headers();
        
        json.put("topic", msg.topic());
        json.put("partition", msg.partition());
        json.put("timestampType", msg.timestampType());
        json.put("timestamp", msg.timestamp());
        json.put("key", msg.key());
        
        if(GeneralHelper.isNotNull(headers))
        {
            headers.forEach(h -> json.put(h.key(), GeneralHelper.bytes2Str(h.value())));
        }
        
        json.put("msg", JSONObject.parseObject(msg.value()));
        
        logMessage(json);
    }
    
    private void logMessage(String msg)
    {
        JSONObject json = new JSONObject();
        json.put("msg", JSONObject.parseObject(msg));
        
        logMessage(json);
    }
    
    private void logMessage(JSONObject json)
    {
        log.info("receive message -> {}", json.toJSONString());
    }

    private void logTextMessage(Message<String> msg)
    {
        JSONObject json = new JSONObject();
        MessageHeaders headers = msg.getHeaders();
        
        json.put("topic", headers.get(KafkaHeaders.RECEIVED_TOPIC));
        json.put("partition", headers.get(KafkaHeaders.RECEIVED_PARTITION));
        json.put("timestampType", headers.get(KafkaHeaders.TIMESTAMP_TYPE));
        json.put("timestamp", headers.get(KafkaHeaders.RECEIVED_TIMESTAMP));
        json.put("groupId", headers.get(KafkaHeaders.GROUP_ID));
        json.put("key", headers.get(KafkaHeaders.RECEIVED_KEY));
        
        json.put("messageId", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_MSG_ID)));
        json.put("sourceRequestId", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_SOURCE_REQUEST_ID)));
        json.put("domainName", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_DOMAIN_NAME)));
        json.put("eventName", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_EVENT_NAME)));
        json.put("correlationId", GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_CORRELATION_ID)));                    

        json.put("msg", msg.getPayload());
        
        logMessage(json);
    }
    

}
