package io.github.hpsocket.soa.starter.websocket.sender.local;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import io.github.hpsocket.soa.starter.websocket.message.JsonWebSocketMessage;
import io.github.hpsocket.soa.starter.websocket.sender.WebSocketMessageSender;
import io.github.hpsocket.soa.starter.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <b>本地的 {@link WebSocketMessageSender} 实现类</b>
 */
@Slf4j
@RequiredArgsConstructor
public class LocalWebSocketMessageSender implements WebSocketMessageSender
{
    private final WebSocketSessionManager sessionManager;

    public void sendWebSocketMessage(Integer userType, Collection<Long> userIds, String messageType, String messageContent)
    {
        Collection<WebSocketSession> sessions = CollUtil.isEmpty(userIds) ? Collections.emptyList() : new ArrayList<>(userIds.size());
        if(userType != null && CollUtil.isNotEmpty(userIds))
        {
            sessions = sessionManager.getSessionList(userType, userIds);
        }
        else if(userType != null)
        {
            sessions = sessionManager.getSessionList(userType);
        }

        if(CollUtil.isEmpty(sessions))
        {
            if(log.isDebugEnabled())
            {
                log.debug("[ws-send] session not match -> (userType: {}, userIds: {}, messageType: {}, messageContent: {})", userType, userIds, messageType, messageContent);
            }
        }

        doSend(sessions, messageType, messageContent);
    }

    public void doSend(Collection<WebSocketSession> sessions, String messageType, String messageContent)
    {
        JsonWebSocketMessage message = new JsonWebSocketMessage().setType(messageType).setContent(messageContent);
        String payload = JSON.toJSONString(message);

        sessions.forEach(session ->
        {
            if(session == null)
            {
                log.error("[ws-send] session is null -> {}", message);
                return;
            }

            if(!session.isOpen())
            {
                log.error("[ws-send] session is closed (sessionId: {}) -> {}", session.getId(), message);
                return;
            }

            try
            {
                session.sendMessage(new TextMessage(payload));
                log.info("[ws-send] send success (sessionId: {}) -> {}", session.getId(), message);
            }
            catch(IOException e)
            {
                log.error("[ws-send] send fail (sessionId: {}) -> exception: {}, message: {}", session.getId(), e.getMessage(), message, e);
            }
        });
    }
}
