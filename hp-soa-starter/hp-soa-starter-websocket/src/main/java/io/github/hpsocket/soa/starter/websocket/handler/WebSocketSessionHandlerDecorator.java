package io.github.hpsocket.soa.starter.websocket.handler;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.websocket.exception.WebSocketExceptionHandler;
import io.github.hpsocket.soa.starter.websocket.properties.SoaWebSocketProperties;
import io.github.hpsocket.soa.starter.websocket.session.WebSocketSessionManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * {@link WebSocketHandler} 的装饰类，实现了以下功能：<br>
 * 1. 管理 {@link WebSocketSession} 连接或关闭事件<br>
 * 2. 封装 {@link WebSocketSession} 支持并发操作
 */
@Slf4j
@Getter
@Setter
public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator
{
    private final WebSocketSessionManager sessionManager;
    private final SoaWebSocketProperties soaWebSocketProperties;

    private AsyncService asyncService;
    private WebSocketExceptionHandler exceptionHandler;

    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate, WebSocketSessionManager sessionManager, SoaWebSocketProperties soaWebSocketProperties)
    {
        super(delegate);
        this.sessionManager = sessionManager;
        this.soaWebSocketProperties = soaWebSocketProperties;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
    {
        session = new ConcurrentWebSocketSessionDecorator(session, soaWebSocketProperties.getMessageSendTimeLimit(), soaWebSocketProperties.getMessageBufferSizeLimit());
        sessionManager.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
    {
        sessionManager.removeSession(session);
        if (session.isOpen()) { try { session.close(); } catch (Exception ex) { } }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception
    {
        log.warn("handleTransportError (sessionId: {}) -> {}", session.getId(), exception.getMessage(), exception);

        super.handleTransportError(session, exception);
        if (session.isOpen()) { try { session.close(); } catch (Exception ex) { } }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();

        try
        {
            mdcAttr.putMdc();
            super.handleMessage(session, message);
        }
        catch(Exception e)
        {
            invokeExceptionHandler(session, message, e);
            throw e;
        }
        finally
        {
            mdcAttr.removeMdc();
        }
    }

    private void invokeExceptionHandler(WebSocketSession session, WebSocketMessage<?> message, Exception e)
    {
        try
        {
            if(exceptionHandler == null)
                return;

            long timestamp = System.currentTimeMillis();

            if(asyncService == null)
                exceptionHandler.handleException(session, message, timestamp, e);
            else
                asyncService.execute(() -> exceptionHandler.handleException(session, message, timestamp, e));
        }
        catch(Exception ex)
        {
            log.error("execute WebSocket Invoke-Exception-Handler exception (sessionId: {}) -> {}", session.getId(), ex.getMessage(), ex);
        }
    }
}
