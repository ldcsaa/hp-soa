package io.github.hpsocket.demo.mq.consumer.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

        long deliveryTag	= properties.getDeliveryTag();
        String msgId		= properties.getMessageId();
        String domainName	= properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName	= properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = (JSONObject)JSON.parse(message.getBody());
            log.info("receive message -> msgId: {}, domainName: {}, evnetName: {}, msg: {}", msgId, domainName, eventName, msg);
            channel.basicAck(deliveryTag, false);
        }
        catch(Exception e)
        {
        	channel.basicNack(deliveryTag, false, false);
        	log.error("receive message fail -> msgId: {}, domainName: {}, evnetName: {}, exception: {}", msgId, domainName, eventName, e.getMessage(), e);
		}
	}
	
	@RabbitListener(queues = {AppConfig.QUE_REGION_1}, ackMode = "AUTO", containerFactory = "firstSimpleRabbitListenerContainerFactory")
	public void onFirstMessage(Message message, Channel channel) throws IOException
	{
        MessageProperties properties = message.getMessageProperties();

        String msgId		= properties.getMessageId();
        String domainName	= properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName	= properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = (JSONObject)JSON.parse(message.getBody());
            log.info("receive message -> msgId: {}, domainName: {}, evnetName: {}, msg: {}", msgId, domainName, eventName, msg);
        }
        catch(Exception e)
        {
        	log.error("receive message fail -> msgId: {}, domainName: {}, evnetName: {}, exception: {}", msgId, domainName, eventName, e.getMessage(), e);
		}
	}
	
}
