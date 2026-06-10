package io.github.hpsocket.soa.starter.websocket.sender.rocketmq;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;

import java.util.Collection;

/**
 * <b>RocketMQ {@link WebSocketMessageSender} 实现类</b>
 */
@Slf4j
@RequiredArgsConstructor
public class RocketMQWebSocketMessageSender implements WebSocketMessageSender
{
    private final RocketMQClientTemplate rocketMQClientTemplate;
    private final String topic;

    @Override
    public void sendWebSocketMessage(Integer userType, Collection<Long> userIds, String messageType, String messageContent)
    {
        SoaWebSocketMessage mqMessage = new SoaWebSocketMessage();
        mqMessage.setUserIds(userIds).setUserType(userType).setMessageType(messageType).setMessageContent(messageContent);
        rocketMQClientTemplate.syncSendNormalMessage(topic, mqMessage);
    }

}
