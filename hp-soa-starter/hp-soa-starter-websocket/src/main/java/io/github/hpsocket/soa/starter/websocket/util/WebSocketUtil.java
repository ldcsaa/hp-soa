package io.github.hpsocket.soa.starter.websocket.util;

import io.github.hpsocket.soa.starter.websocket.model.SoaWebSocketUser;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * WebSocket 工具类
 */
public class WebSocketUtil
{

    public static final String MESSAGE_PING = "$ping";
    public static final String MESSAGE_PONG = "$pong";
    public static final String ATTRIBUTE_LOGIN_USER = "__WS_LOGIN_USER__";

    /**
     * 设置当前用户
     *
     * @param loginUser  登录用户
     * @param attributes Session
     */
    public static void setLoginUser(SoaWebSocketUser loginUser, Map<String, Object> attributes)
    {
        attributes.put(ATTRIBUTE_LOGIN_USER, loginUser);
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    public static SoaWebSocketUser getLoginUser(WebSocketSession session)
    {
        return (SoaWebSocketUser)session.getAttributes().get(ATTRIBUTE_LOGIN_USER);
    }

    /**
     * 获得当前用户的ID
     *
     * @return 用户ID
     */
    public static Long getLoginUserId(WebSocketSession session)
    {
        SoaWebSocketUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获得当前用户的ID
     *
     * @return 用户ID
     */
    public static String getLoginUserName(WebSocketSession session)
    {
        SoaWebSocketUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getUserName() : null;
    }

    /**
     * 获得当前用户的类型
     *
     * @return 用户编号
     */
    public static Integer getLoginUserType(WebSocketSession session)
    {
        SoaWebSocketUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getUserType() : null;
    }

    /**
     * 获得当前用户的组织ID
     *
     * @return 用户编号
     */
    public static Long getLoginUserGroupId(WebSocketSession session)
    {
        SoaWebSocketUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getGroupId() : null;
    }

}
