package io.github.hpsocket.soa.starter.websocket.sender.rabbitmq;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.local.LocalWebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * {@link SoaWebSocketMessage} 广播消息的消费者，负责 WebSocket 消息实际发送操作
 */
@RequiredArgsConstructor
public class RabbitMQWebSocketMessageConsumer
{
    private final LocalWebSocketMessageSender localWebSocketMessageSender;

    @RabbitListener(
            bindings = @QueueBinding(
                            value = @Queue(
                                        name = "${hp.soa.websocket.message-topic}" + ":" + "#{T(java.util.UUID).randomUUID()}",
                                        autoDelete = "true"
                                    ),
                            exchange = @Exchange(
                                    name = "${soa-websocket-message-exchange}",
                                    type = ExchangeTypes.TOPIC,
                                    declare = "false"
                            )
            )
    )
    public void onMessage(SoaWebSocketMessage message)
    {
        localWebSocketMessageSender.sendWebSocketMessage(message.getUserType(), message.getUserIds(), message.getMessageType(), message.getMessageContent());
    }

}
