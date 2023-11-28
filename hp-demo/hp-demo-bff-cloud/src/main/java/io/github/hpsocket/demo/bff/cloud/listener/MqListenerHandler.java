package io.github.hpsocket.demo.bff.cloud.listener;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.HEADER_DOMAIN_NAME;
import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.HEADER_EVENT_NAME;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;

import io.github.hpsocket.demo.bff.cloud.config.AppConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqListenerHandler
{
    @RabbitListener(queues = {AppConfig.USER_QUEUE}, ackMode = "MANUAL", autoStartup = "false")
    public void onUserMessage(Message message, Channel channel) throws IOException
    {
        MessageProperties properties = message.getMessageProperties();

        String msgId        = properties.getMessageId();
        long deliveryTag    = properties.getDeliveryTag();
        String domainName   = properties.getHeader(HEADER_DOMAIN_NAME);
        String eventName    = properties.getHeader(HEADER_EVENT_NAME);
        
        try
        {
            JSONObject msg = JSON.parseObject(message.getBody());

            log.info("receive message -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, msg: {}", AppConfig.USER_QUEUE, msgId, domainName, eventName, msg.toJSONString());
            channel.basicAck(deliveryTag, false);
        }
        catch(Exception e)
        {
            channel.basicNack(deliveryTag, false, false);
            log.error("receive message fail -> queue: {}, msgId: {}, domainName: {}, evnetName: {}, exception: {}", AppConfig.USER_QUEUE, msgId, domainName, eventName, e.getMessage(), e);
        }
    }


}
