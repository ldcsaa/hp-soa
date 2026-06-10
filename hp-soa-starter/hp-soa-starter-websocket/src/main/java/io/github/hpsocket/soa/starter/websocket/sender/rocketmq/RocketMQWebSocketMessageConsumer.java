package io.github.hpsocket.soa.starter.websocket.sender.rocketmq;

import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqHelper;
import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.local.LocalWebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;

/**
 * {@link SoaWebSocketMessage} 广播消息的消费者，负责 WebSocket 消息实际发送操作
 */
@RocketMQMessageListener
                        (
                            topic = "${hp.soa.websocket.message-topic}",
                            consumerGroup = "${hp.soa.websocket.message-consumer-group}" + ":" + "#{T(java.util.UUID).randomUUID()}"
                        )
@RequiredArgsConstructor
public class RocketMQWebSocketMessageConsumer implements RocketMQListener
{
    private final LocalWebSocketMessageSender localWebSocketMessageSender;

    @Override
    public ConsumeResult consume(MessageView messageView)
    {
        SoaWebSocketMessage message = RocketmqHelper.getMessageViewBodyAsObject(messageView, SoaWebSocketMessage.class);
        localWebSocketMessageSender.sendWebSocketMessage(message.getUserType(), message.getUserIds(), message.getMessageType(), message.getMessageContent());

        return ConsumeResult.SUCCESS;
    }

}
