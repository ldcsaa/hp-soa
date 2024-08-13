package io.github.hpsocket.soa.starter.kafka.util;

import org.springframework.kafka.support.KafkaHeaders;

/** <b>HP-SOA Kafka 常量</b> */
public class KafkaConstant
{
    /** 消息头：领域名称 */
    public static final String HEADER_DOMAIN_NAME       = "x-domain-name";
    /** 消息头：事件名称 */
    public static final String HEADER_EVENT_NAME        = "x-event-name";
    /** 消息头：消息 ID */
    public static final String HEADER_MSG_ID            = "x-msg-id";
    /** 消息头：源请求 ID */
    public static final String HEADER_SOURCE_REQUEST_ID = "x-source-request-id";
    /** 消息头：关联数据 ID */
    public static final String HEADER_CORRELATION_ID    = KafkaHeaders.CORRELATION_ID;
    
    /** 消息发送状态：未发送 */
    public static final int SF_NOT_SEND     = 0;
    /** 消息发送状态：正在发送 */
    public static final int SF_SENDING      = 1;
    /** 消息发送状态：发送成功 */
    public static final int SF_SEND_SUCC    = 2;
    /** 消息发送状态：发送失败 */
    public static final int SF_SEND_FAIL    = 3;

}
