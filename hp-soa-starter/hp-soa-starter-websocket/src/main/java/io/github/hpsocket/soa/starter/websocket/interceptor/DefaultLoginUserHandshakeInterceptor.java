package io.github.hpsocket.soa.starter.websocket.interceptor;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketUser;
import io.github.hpsocket.soa.starter.websocket.util.WebSocketUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * <b>{@link HandshakeInterceptor} 默认实现类</b><br>
 * {@link DefaultLoginUserHandshakeInterceptor} 负责把 {@link SoaWebSocketUser} 添加到 {@link WebSocketSession}
 */
public class DefaultLoginUserHandshakeInterceptor implements HandshakeInterceptor
{

    protected SoaWebSocketUser getCurrentWebSocketUser(ServerHttpRequest request, ServerHttpResponse response)
    {
        return null;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes)
    {
        SoaWebSocketUser loginUser = getCurrentWebSocketUser(request, response);

        if(loginUser == null)
        {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        WebSocketUtils.setLoginUser(loginUser, attributes);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception)
    {
        // do nothing
    }

}
