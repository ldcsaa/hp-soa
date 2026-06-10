package io.github.hpsocket.soa.starter.websocket.sender.kafka;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;

/**
 * <b>Kafka {@link WebSocketMessageSender} 实现类</b>
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaWebSocketMessageSender implements WebSocketMessageSender
{
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String topic;

    @Override
    public void sendWebSocketMessage(Integer userType, Collection<Long> userIds, String messageType, String messageContent)
    {
        SoaWebSocketMessage message = new SoaWebSocketMessage();
        message.setUserType(userType).setUserIds(userIds).setMessageType(messageType).setMessageContent(messageContent);

        try
        {
            kafkaTemplate.send(topic, message).get();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
