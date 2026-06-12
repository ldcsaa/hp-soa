package io.github.hpsocket.soa.starter.websocket.handler;

import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson2.JSONObject;
import io.github.hpsocket.soa.starter.websocket.listener.WebSocketMessageListener;
import io.github.hpsocket.soa.starter.websocket.message.JsonWebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JSON 格式 {@link WebSocketHandler} 实现类
 * 基于 {@link JsonWebSocketMessage#getType()} 消息类型，调度到对应的 {@link WebSocketMessageListener} 监听器。
 */
@Slf4j
public class JsonWebSocketMessageHandler extends TextWebSocketHandler
{
    private final Map<String, WebSocketMessageListener<Object>> listeners = new HashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonWebSocketMessageHandler(List<? extends WebSocketMessageListener> listenersList)
    {
        listenersList.forEach((Consumer<WebSocketMessageListener>)listener -> listeners.put(listener.getType(), listener));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
    {
        if(message.getPayloadLength() == 0)
        {
            return;
        }

        if(handleSpecialMessage(session, message))
        {
            return;
        }

        String payload = message.getPayload();
        JsonWebSocketMessage jsonMessage = JSONObject.parseObject(payload, JsonWebSocketMessage.class);

        if(jsonMessage == null)
        {
            log.error("handleTextMessage error, message is empty (sessionId: {}) -> {}", session.getId(), payload);
            return;
        }

        if(StringUtils.isBlank(jsonMessage.getType()))
        {
            log.error("handleTextMessage error, missing message type (sessionId: {}) -> {}", session.getId(), payload);
            return;
        }

        WebSocketMessageListener<Object> messageListener = listeners.get(jsonMessage.getType());

        if(messageListener == null)
        {
            log.error("handleTextMessage error, missing message listener (sessionId: {}) -> {}", session.getId(), payload);
            return;
        }

        Type type = TypeUtil.getTypeArgument(messageListener.getClass(), 0);
        Object messageObj = JSONObject.parseObject(jsonMessage.getContent(), type);

        messageListener.onMessage(session, messageObj);
    }

    private static boolean handleSpecialMessage(WebSocketSession session, TextMessage message) throws IOException
    {
        if(message.getPayloadLength() == 4 && Objects.equals(message.getPayload(), "ping"))
        {
            session.sendMessage(new TextMessage("pong"));
            return true;
        }
        return false;
    }
}
