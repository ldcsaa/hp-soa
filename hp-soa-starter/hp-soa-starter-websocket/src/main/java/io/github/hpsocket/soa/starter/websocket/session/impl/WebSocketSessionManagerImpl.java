package io.github.hpsocket.soa.starter.websocket.session.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import io.github.hpsocket.soa.starter.websocket.listener.WebSocketSessionListener;
import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketUser;
import io.github.hpsocket.soa.starter.websocket.session.WebSocketSessionManager;
import io.github.hpsocket.soa.starter.websocket.util.WebSocketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认的 {@link WebSocketSessionManager} 实现类
 */
@RequiredArgsConstructor
public class WebSocketSessionManagerImpl implements WebSocketSessionManager
{
    private final WebSocketSessionListener webSocketSessionListener;

    /**
     * id 与 WebSocketSession 映射
     * key：Session 编号
     */
    private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

    /**
     * user 与 WebSocketSession 映射
     * key1：用户类型
     * key2：用户编号
     */
    private final ConcurrentMap<Integer, ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session)
    {
        SoaWebSocketUser user = WebSocketUtil.getLoginUser(session);

        if(user == null)
        {
            return;
        }

        idSessions.put(session.getId(), session);

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());

        if(userSessionsMap == null)
        {
            userSessionsMap = new ConcurrentHashMap<>();
            if(userSessions.putIfAbsent(user.getUserType(), userSessionsMap) != null)
            {
                userSessionsMap = userSessions.get(user.getUserType());
            }
        }

        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getUserId());

        if(sessions == null)
        {
            sessions = new CopyOnWriteArrayList<>();
            if(userSessionsMap.putIfAbsent(user.getUserId(), sessions) != null)
            {
                sessions = userSessionsMap.get(user.getUserId());
            }
        }

        sessions.add(session);

        if(webSocketSessionListener != null)
        {
            webSocketSessionListener.onAddSession(session, user);
        }
    }

    @Override
    public void removeSession(WebSocketSession session)
    {
        idSessions.remove(session.getId());

        SoaWebSocketUser user = WebSocketUtil.getLoginUser(session);

        if(user == null)
        {
            return;
        }

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());

        if(userSessionsMap == null)
        {
            return;
        }

        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getUserId());
        sessions.removeIf(session0 -> session0.getId().equals(session.getId()));

        if(CollUtil.isEmpty(sessions))
        {
            userSessionsMap.remove(user.getUserId(), sessions);
        }

        if(webSocketSessionListener != null)
        {
            webSocketSessionListener.onRemoveSession(session, user);
        }
    }

    @Override
    public WebSocketSession getSession(String id)
    {
        return idSessions.get(id);
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType)
    {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);

        if(CollUtil.isEmpty(userSessionsMap))
        {
            return List.of();
        }

        LinkedList<WebSocketSession> result = new LinkedList<>();

        for(List<WebSocketSession> sessions : userSessionsMap.values())
        {
            if(CollUtil.isNotEmpty(sessions))
            {
                result.addAll(sessions);
            }
        }

        return result;
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType, Collection<Long> userIds)
    {
        List<WebSocketSession> sessions = new ArrayList<>(userIds.size());
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);

        if(MapUtil.isNotEmpty(userSessionsMap))
        {
            for(Long userId : userIds)
            {
                CopyOnWriteArrayList<WebSocketSession> wsSessions = userSessionsMap.get(userId);
                if(CollUtil.isNotEmpty(wsSessions))
                {
                    sessions.addAll(wsSessions);
                }
            }
        }

        return sessions;
    }

}
