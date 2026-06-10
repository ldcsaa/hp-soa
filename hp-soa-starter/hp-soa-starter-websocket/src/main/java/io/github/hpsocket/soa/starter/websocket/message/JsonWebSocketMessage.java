package io.github.hpsocket.soa.starter.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * JSON 格式的 WebSocket 消息帧
 */
@Data
@Accessors(chain = true)
public class JsonWebSocketMessage implements Serializable
{
    /**
     * <b>消息类型</b>
     */
    private String type;
    /**
     * <b>消息内容</b> JSON 格式
     */
    private String content;

}
