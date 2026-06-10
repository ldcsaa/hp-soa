package io.github.hpsocket.soa.starter.websocket.sender;

import com.alibaba.fastjson2.JSON;

import java.util.Collection;
import java.util.List;

/**
 * WebSocket 消息的发送器接口
 */
public interface WebSocketMessageSender
{

    /**
     * 发送消息给指定用户
     *
     * @param userType       用户类型
     * @param userId         用户 ID
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    default void send(Integer userType, Long userId, String messageType, String messageContent)
    {
        sendWebSocketMessage(userType, List.of(userId), messageType, messageContent);
    }

    /**
     * 批量发送消息给指定用户列表
     *
     * @param userType       用户类型
     * @param userIds        用户 ID 列表
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 封装对象
     */
    default void send(Integer userType, Collection<Long> userIds, String messageType, String messageContent)
    {
        sendWebSocketMessage(userType, userIds, messageType, messageContent);
    }

    /**
     * 发送消息给指定用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    default void send(Integer userType, String messageType, String messageContent)
    {
        sendWebSocketMessage(userType, null, messageType, messageContent);
    }

    /**
     * 发送消息给指定用户
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 封装对象
     */
    default void sendObject(Integer userType, Long userId, String messageType, Object messageContent)
    {
        send(userType, userId, messageType, JSON.toJSONString(messageContent));
    }

    /**
     * 发送消息给指定用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 封装对象
     */
    default void sendObject(Integer userType, String messageType, Object messageContent)
    {
        send(userType, messageType, JSON.toJSONString(messageContent));
    }

    /**
     * 批量发送消息给指定用户列表
     *
     * @param userType       用户类型
     * @param userIds        用户 ID 列表
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 封装对象
     */
    default void sendObject(Integer userType, Collection<Long> userIds, String messageType, Object messageContent)
    {
        send(userType, userIds, messageType, JSON.toJSONString(messageContent));
    }

    /**
     * 批量发送消息给指定用户列表
     *
     * @param userType       用户类型
     * @param userIds        用户 ID 列表
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 封装对象
     */
    void sendWebSocketMessage(Integer userType, Collection<Long> userIds, String messageType, String messageContent);
}
