package io.github.hpsocket.soa.starter.websocket.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;

/**
 * WebSocket 的消息
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class SoaWebSocketMessage implements Serializable
{
    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户 ID 列表
     */
    private Collection<Long> userIds;

    /**
     * 消息标签
     */
    private String messageTag;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String messageContent;
}
