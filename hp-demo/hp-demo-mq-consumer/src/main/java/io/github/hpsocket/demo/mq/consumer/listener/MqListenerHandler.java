package io.github.hpsocket.demo.mq.consumer.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;

import io.github.hpsocket.demo.mq.consumer.config.AppConfig;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

import java.io.IOException;

@Slf4j
@Component
public class MqListenerHandler
{
	@RabbitListener(queues = {AppConfig.QUE_REGION_0}, ackMode = "MANUAL", containerFactory = "defaultSimpleRabbitListenerContainerFactory")
	public void onDefaultMessage(Message message, Channel channel) throws IOException
	{
        MessageProperties properties = message.getMessageProperties();

        String msgId		= properties.getMessageId();
        long deliveryTag	= properties.getDeliveryTag();
        String domainName	= properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName	= properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
        	JSONObject msg = JSON.parseObject(message.getBody());

            log.info("receive message -> msgId: {}, domainName: {}, evnetName: {}, msg: {}", msgId, domainName, eventName, msg.toJSONString());
            channel.basicAck(deliveryTag, false);
        }
        catch(Exception e)
        {
        	channel.basicNack(deliveryTag, false, false);
        	log.error("receive message fail -> msgId: {}, domainName: {}, evnetName: {}, exception: {}", msgId, domainName, eventName, e.getMessage(), e);
		}
	}
	
	@RabbitListener(queues = {AppConfig.QUE_REGION_1}, ackMode = "AUTO", containerFactory = "firstSimpleRabbitListenerContainerFactory")
	public void onFirstMessage(org.springframework.messaging.Message<JSONObject> message, Channel channel) throws IOException
	{
		MessageHeaders headers = message.getHeaders();
		String msgId 		= (String)headers.get(HEADER_AMQP_MESSAGE_ID);
        //long deliveryTag	= (Long)headers.get(HEADER_AMQP_DELIVERY_TAG);
		String domainName	= (String)headers.get(HEADER_DOMAIN_NAME);
		String eventName	= (String)headers.get(HEADER_EVENT_NAME);
        
        try
        {
        	JSONObject msg = message.getPayload();
        	
            log.info("receive message -> msgId: {}, domainName: {}, evnetName: {}, msg: {}", msgId, domainName, eventName, msg.toJSONString());
        }
        catch(Exception e)
        {
        	log.error("receive message fail -> msgId: {}, domainName: {}, evnetName: {}, exception: {}", msgId, domainName, eventName, e.getMessage(), e);
		}
	}
	
	@RabbitListener(queues = {AppConfig.QUE_REGION_2}, ackMode = "AUTO", containerFactory = "secondSimpleRabbitListenerContainerFactory")
	public void onSecondMessage(Message message, Channel channel) throws IOException
	{
        MessageProperties properties = message.getMessageProperties();

        String msgId		= properties.getMessageId();
        //long deliveryTag	= properties.getDeliveryTag();
        String domainName	= properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName	= properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
        	JSONObject msg = JSON.parseObject(message.getBody());

            log.info("receive message -> msgId: {}, domainName: {}, evnetName: {}, msg: {}", msgId, domainName, eventName, msg.toJSONString());
        }
        catch(Exception e)
        {
        	log.error("receive message fail -> msgId: {}, domainName: {}, evnetName: {}, exception: {}", msgId, domainName, eventName, e.getMessage(), e);
		}
	}
	
	@RabbitListener(queues = {AppConfig.QUE_REGION_3}, ackMode = "MANUAL", containerFactory = "thirdSimpleRabbitListenerContainerFactory")
	public void onThirdMessage(org.springframework.messaging.Message<JSONObject> message, Channel channel) throws IOException
	{
		MessageHeaders headers = message.getHeaders();
		String msgId 		= (String)headers.get(HEADER_AMQP_MESSAGE_ID);
        long deliveryTag	= (Long)headers.get(HEADER_AMQP_DELIVERY_TAG);
		String domainName	= (String)headers.get(HEADER_DOMAIN_NAME);
		String eventName	= (String)headers.get(HEADER_EVENT_NAME);
        
        try
        {
        	JSONObject msg = message.getPayload();
        	
            log.info("receive message -> msgId: {}, domainName: {}, evnetName: {}, msg: {}", msgId, domainName, eventName, msg.toJSONString());
            channel.basicAck(deliveryTag, false);
        }
        catch(Exception e)
        {
        	channel.basicNack(deliveryTag, false, false);
        	log.error("receive message fail -> msgId: {}, domainName: {}, evnetName: {}, exception: {}", msgId, domainName, eventName, e.getMessage(), e);
		}
	}
	
}
