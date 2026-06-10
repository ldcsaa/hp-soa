package io.github.hpsocket.soa.starter.websocket.listener;

import io.github.hpsocket.soa.starter.websocket.message.JsonWebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * <b>WebSocket 消息监听器接口</b>
 * 前端发送消息给后端后，处理对应 {@link #getType()} 类型的消息
 *
 * @param <T> 泛型，消息类型
 */
public interface WebSocketMessageListener<T>
{
    /**
     * 消息处理入口
     *
     * @param session Session
     * @param message 消息
     */
    void onMessage(WebSocketSession session, T message);

    /**
     * 获取消息类型
     *
     * @return 消息类型
     * @see JsonWebSocketMessage#getType()
     */
    String getType();

}
