package io.github.hpsocket.soa.starter.rocketmq.util;

import org.apache.rocketmq.client.support.RocketMQHeaders;

/** <b>HP-SOA RocketMQ 常量</b> */
public class RocketmqConstant
{
    /** 消息头：领域名称 */
    public static final String HEADER_DOMAIN_NAME       = "x-domain-name";
    /** 消息头：事件名称 */
    public static final String HEADER_EVENT_NAME        = "x-event-name";
    /** 消息头：消息 ID */
    public static final String HEADER_MSG_ID            = "x-msg-id";
    /** 消息头：源请求 ID */
    public static final String HEADER_SOURCE_REQUEST_ID = "x-source-request-id";
    /** 消息头：消息时间戳 */
    public static final String HEADER_MSG_TIMESTAMP     = "x-msg-timestamp";
    /** 消息头：重试次数 */
    public static final String HEADER_RETRIES           = "x-retries";
    /** 消息头：关联数据 ID */
    public static final String HEADER_CORRELATION_ID    = "x-correlation-id";
    /** 消息头：RocketMQ Keys */
    public static final String HEADER_KEYS              = RocketMQHeaders.PREFIX + RocketMQHeaders.KEYS;

}
