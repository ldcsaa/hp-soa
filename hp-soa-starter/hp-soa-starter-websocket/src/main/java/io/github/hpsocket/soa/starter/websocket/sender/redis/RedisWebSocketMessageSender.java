package io.github.hpsocket.soa.starter.websocket.sender.redis;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.WebSocketMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.Collection;

/**
 * 基于 Redis 的 {@link WebSocketMessageSender} 实现类
 */
@Slf4j
public class RedisWebSocketMessageSender implements WebSocketMessageSender
{
    private final RTopic rTopic;

    public RedisWebSocketMessageSender(RedissonClient redissonClient, String topic)
    {
        rTopic = redissonClient.getTopic(topic);
    }

    @Override
    public void sendWebSocketMessage(Integer userType, Collection<Long> userIds, String messageType, String messageContent)
    {
        SoaWebSocketMessage message = new SoaWebSocketMessage();
        message.setUserIds(userIds).setUserType(userType).setMessageType(messageType).setMessageContent(messageContent);
        rTopic.publish(message);
    }

}
