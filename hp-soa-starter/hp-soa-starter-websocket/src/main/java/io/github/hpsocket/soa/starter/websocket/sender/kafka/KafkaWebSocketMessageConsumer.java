package io.github.hpsocket.soa.starter.websocket.sender.kafka;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.local.LocalWebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * {@link SoaWebSocketMessage} 广播消息的消费者，负责 WebSocket 消息实际发送操作
 */
@RequiredArgsConstructor
public class KafkaWebSocketMessageConsumer
{
    private final LocalWebSocketMessageSender localWebSocketMessageSender;

    @KafkaListener(
                    topics = "${hp.soa.websocket.message-topic}",
                    groupId = "${hp.soa.websocket.message-consumer-group}" + ":" + "#{T(java.util.UUID).randomUUID()}"
                  )
    public void onMessage(SoaWebSocketMessage message)
    {
        localWebSocketMessageSender.sendWebSocketMessage(message.getUserType(), message.getUserIds(), message.getMessageType(), message.getMessageContent());
    }

}
