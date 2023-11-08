package io.github.hpsocket.demo.mq.consumer.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.stream.MessageHandler.Context;
import com.rabbitmq.stream.Properties;

import io.github.hpsocket.demo.mq.consumer.config.AppConfig;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class MqListenerHandler
{
    @RabbitListener(queues = {AppConfig.QUE_REGION_0}, ackMode = "MANUAL", autoStartup = "false", containerFactory = "defaultSimpleRabbitListenerContainerFactory")
    public void onDefaultMessage(Message message, Channel channel) throws IOException
    {
        MessageProperties properties = message.getMessageProperties();

        String msgId        = properties.getMessageId();
        long deliveryTag    = properties.getDeliveryTag();
        String domainName   = properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName    = properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = JSON.parseObject(message.getBody());

            log.info("receive message -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, msg: {}", AppConfig.QUE_REGION_0, msgId, domainName, eventName, msg.toJSONString());
            channel.basicAck(deliveryTag, false);
        }
        catch(Exception e)
        {
            channel.basicNack(deliveryTag, false, false);
            log.error("receive message fail -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, exception: {}", AppConfig.QUE_REGION_0, msgId, domainName, eventName, e.getMessage(), e);
        }
    }

    @RabbitListener(queues = {AppConfig.QUE_REGION_1}, ackMode = "AUTO", autoStartup = "false", containerFactory = "firstSimpleRabbitListenerContainerFactory")
    public void onFirstMessage(org.springframework.messaging.Message<JSONObject> message, Channel channel) throws IOException
    {
        MessageHeaders headers = message.getHeaders();
        String msgId        = (String)headers.get(HEADER_AMQP_MESSAGE_ID);
        //long deliveryTag  = (Long)headers.get(HEADER_AMQP_DELIVERY_TAG);
        String domainName   = (String)headers.get(HEADER_DOMAIN_NAME);
        String eventName    = (String)headers.get(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = message.getPayload();
            
            log.info("receive message -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, msg: {}", AppConfig.QUE_REGION_1, msgId, domainName, eventName, msg.toJSONString());
        }
        catch(Exception e)
        {
            log.error("receive message fail -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, exception: {}", AppConfig.QUE_REGION_1, msgId, domainName, eventName, e.getMessage(), e);
        }
    }

    /*
    @RabbitListener(queues = {AppConfig.QUE_REGION_2}, ackMode = "AUTO", autoStartup = "false", containerFactory = "secondSimpleRabbitListenerContainerFactory")
    public void onSecondMessage(Message message, Channel channel) throws IOException
    {
        MessageProperties properties = message.getMessageProperties();

        String msgId        = properties.getMessageId();
        //long deliveryTag  = properties.getDeliveryTag();
        String domainName   = properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName    = properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = JSON.parseObject(message.getBody());

            log.info("receive message -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, msg: {}", AppConfig.QUE_REGION_2, msgId, domainName, eventName, msg.toJSONString());
        }
        catch(Exception e)
        {
            log.error("receive message fail -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, exception: {}", AppConfig.QUE_REGION_2, msgId, domainName, eventName, e.getMessage(), e);
        }
    }
    
    @RabbitListener(queues = {AppConfig.QUE_REGION_3}, ackMode = "MANUAL", autoStartup = "false", containerFactory = "thirdSimpleRabbitListenerContainerFactory")
    public void onThirdMessage(org.springframework.messaging.Message<JSONObject> message, Channel channel) throws IOException
    {
        MessageHeaders headers = message.getHeaders();
        String msgId        = (String)headers.get(HEADER_AMQP_MESSAGE_ID);
        long deliveryTag    = (Long)headers.get(HEADER_AMQP_DELIVERY_TAG);
        String domainName   = (String)headers.get(HEADER_DOMAIN_NAME);
        String eventName    = (String)headers.get(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = message.getPayload();
            
            log.info("receive message -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, msg: {}", AppConfig.QUE_REGION_3, msgId, domainName, eventName, msg.toJSONString());
            channel.basicAck(deliveryTag, false);
        }
        catch(Exception e)
        {
            channel.basicNack(deliveryTag, false, false);
            log.error("receive message fail -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, exception: {}", AppConfig.QUE_REGION_3, msgId, domainName, eventName, e.getMessage(), e);
        }
    }
    
    @RabbitListener(queues = {AppConfig.STM_REGION_0}, autoStartup = "false", containerFactory = "defaultStreamRabbitListenerContainerFactory")
    public void onDefaultStreamMessage(com.rabbitmq.stream.Message message, Context context) throws IOException
    {
        onStreamMessage(AppConfig.STM_REGION_0, message, context);
    }
    
    @RabbitListener(queues = {AppConfig.STM_REGION_1}, autoStartup = "false", containerFactory = "firstStreamRabbitListenerContainerFactory")
    public void onFirstStreamMessage(com.rabbitmq.stream.Message message, Context context) throws IOException
    {
        onStreamMessage(AppConfig.STM_REGION_1, message, context);
    }
    */
    
    @RabbitListener(queues = {AppConfig.STM_REGION_2}, autoStartup = "false", containerFactory = "secondStreamRabbitListenerContainerFactory")
    public void onSecondStreamMessage(com.rabbitmq.stream.Message message, Context context) throws IOException
    {
        onStreamMessage(AppConfig.STM_REGION_2, message, context);
    }
    
    @RabbitListener(queues = {AppConfig.STM_REGION_3}, autoStartup = "false", containerFactory = "thirdStreamRabbitListenerContainerFactory", messageConverter = "rabbitMessageConverter")
    public void onThirdStreamMessage(org.springframework.messaging.Message<JSONObject> message) throws IOException
    {
        onStreamMessage(AppConfig.STM_REGION_3, message);
    }
    
    private void onStreamMessage(String stream, com.rabbitmq.stream.Message message, Context context)
    {
        Properties props = message.getProperties();
        Map<String, Object> appProps = message.getApplicationProperties();

        String msgId        = props.getMessageIdAsString();
        String corId        = props.getCorrelationIdAsString();
        String domainName   = (String)appProps.get(HEADER_DOMAIN_NAME);
        String eventName    = (String)appProps.get(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = JSON.parseObject(message.getBodyAsBinary());

            log.info("receive message -> stream: {}, msgId: {}, corId: {}, domainName: {}, evnetName: {}, msg: {}", stream, msgId, corId, domainName, eventName, msg.toJSONString());
        }
        catch(Exception e)
        {
            log.error("receive message fail -> stream: {}, msgId: {}, corId: {}, domainName: {}, evnetName: {}, exception: {}", stream, msgId, corId, domainName, eventName, e.getMessage(), e);
        }
    }
    
    private void onStreamMessage(String stream, org.springframework.messaging.Message<JSONObject> message)
    {
        MessageHeaders headers = message.getHeaders();

        String msgId        = (String)headers.get(HEADER_MSG_ID);
        String corId        = (String)headers.get(HEADER_CORRELA_DATA_ID);
        String domainName   = (String)headers.get(HEADER_DOMAIN_NAME);
        String eventName    = (String)headers.get(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = message.getPayload();

            log.info("receive message -> stream: {}, msgId: {}, corId: {}, domainName: {}, evnetName: {}, msg: {}", stream, msgId, corId, domainName, eventName, msg.toJSONString());
        }
        catch(Exception e)
        {
            log.error("receive message fail -> stream: {}, msgId: {}, corId: {}, domainName: {}, evnetName: {}, exception: {}", stream, msgId, corId, domainName, eventName, e.getMessage(), e);
        }
    }

}
