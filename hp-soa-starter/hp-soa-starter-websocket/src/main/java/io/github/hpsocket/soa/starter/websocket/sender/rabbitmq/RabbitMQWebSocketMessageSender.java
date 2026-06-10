package io.github.hpsocket.soa.starter.websocket.sender.rabbitmq;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Collection;

/**
 * <b>RabbitMQ {@link WebSocketMessageSender} 实现类</b>
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMQWebSocketMessageSender implements WebSocketMessageSender
{
    private final RabbitTemplate rabbitTemplate;
    private final String topic;

    @Override
    public void sendWebSocketMessage(Integer userType, Collection<Long> userIds, String messageType, String messageContent)
    {
        SoaWebSocketMessage mqMessage = new SoaWebSocketMessage();
        mqMessage.setUserIds(userIds).setUserType(userType).setMessageType(messageType).setMessageContent(messageContent);
        rabbitTemplate.convertAndSend(topic, null, mqMessage);
    }

}
