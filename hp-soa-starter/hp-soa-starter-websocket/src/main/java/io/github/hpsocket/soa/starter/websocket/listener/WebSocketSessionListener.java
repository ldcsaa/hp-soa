package io.github.hpsocket.soa.starter.websocket.listener;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketUser;
import org.springframework.web.socket.WebSocketSession;

/**
 * <b>WebSocket Session 监听器接口</b>
 * 处理 WebSocket Session 创建或关闭事件
 */
public interface WebSocketSessionListener
{
    /**
     * 创建 {@linkplain WebSocketSession}
     *
     * @param session Session
     * @param user WebSocket 用户
     */
    void onAddSession(WebSocketSession session, SoaWebSocketUser user);

    /**
     * 关闭 {@linkplain WebSocketSession}
     *
     * @param session Session
     * @param user WebSocket 用户
     */
    void onRemoveSession(WebSocketSession session, SoaWebSocketUser user);

}
