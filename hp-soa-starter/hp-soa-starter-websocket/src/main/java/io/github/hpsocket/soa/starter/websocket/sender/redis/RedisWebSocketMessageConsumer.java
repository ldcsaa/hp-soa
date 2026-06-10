package io.github.hpsocket.soa.starter.websocket.sender.redis;

import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.local.LocalWebSocketMessageSender;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

/**
 * {@link SoaWebSocketMessage} 广播消息的消费者，负责 WebSocket 消息实际发送操作
 */
public class RedisWebSocketMessageConsumer implements MessageListener<SoaWebSocketMessage>
{
    private final LocalWebSocketMessageSender localWebSocketMessageSender;
    private final RTopic rTopic;

    private RedisWebSocketMessageConsumer selfBean;

    public RedisWebSocketMessageConsumer(LocalWebSocketMessageSender localWebSocketMessageSender, RedissonClient redissonClient, String topic)
    {
        this.localWebSocketMessageSender = localWebSocketMessageSender;

        rTopic = redissonClient.getTopic(topic);
        rTopic.addListener(SoaWebSocketMessage.class, this);
    }

    @PostConstruct
    public void postConstruct()
    {
        selfBean = SpringContextHolder.getBean(RedisWebSocketMessageConsumer.class);
    }

    @Override
    public void onMessage(CharSequence channel, SoaWebSocketMessage message)
    {
        selfBean.onMessage(channel, message, rTopic);
    }

    public void onMessage(CharSequence channel, SoaWebSocketMessage message, RTopic rTopic)
    {
        localWebSocketMessageSender.sendWebSocketMessage(message.getUserType(), message.getUserIds(), message.getMessageType(), message.getMessageContent());
    }
}
