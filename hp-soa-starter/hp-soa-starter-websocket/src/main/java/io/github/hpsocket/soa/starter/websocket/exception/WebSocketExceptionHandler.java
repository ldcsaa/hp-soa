package io.github.hpsocket.soa.starter.websocket.exception;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/** <b>WebSocket 异常处理器接口</b> */
public interface WebSocketExceptionHandler
{
    void handleException(WebSocketSession session, WebSocketMessage<?> message, long timestamp, Exception e);
}
