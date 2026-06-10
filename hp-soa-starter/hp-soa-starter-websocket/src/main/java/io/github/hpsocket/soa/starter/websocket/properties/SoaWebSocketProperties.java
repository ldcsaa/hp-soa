package io.github.hpsocket.soa.starter.websocket.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebSocket 配置类
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "hp.soa.websocket")
public class SoaWebSocketProperties
{
    /**
     * 连接路径
     */
    private String path = "/ws";

    /**
     * 消息发送器类型（可选值：redis、rocketmq、rabbitmq、kafka）
     */
    private String senderType = "redis";

    /**
     * WebSocket 消息主题（或队列）
     */
    private String messageTopic = "soa-websocket-message-topic";

    /**
     * WebSocket 消息交换机
     */
    private String messageExchange = "soa-websocket-message-exchange";

    /**
     * WebSocket 消息消费者组
     */
    private String messageConsumerGroup = "soa-websocket-message-consumer-group";

    /**
     * 是否开启 WebSocket 消息接收 Consumer
     */
    private Boolean enableMessageConsumer = Boolean.FALSE;

    /**
     * 默认消息发送超时时间（默认值：1000 * 5）
     */
    private Integer messageSendTimeLimit  = 1000 * 5;

    /**
     * 默认消息发送缓冲上线（默认值：1024 * 100）
     */
    private Integer messageBufferSizeLimit = 1024 * 100;

    /**
     * 允许的跨域来源
     */
    private String[] allowedOrigins = new String[0];

    /**
     * 允许的跨域来源模式（默认：所有来源）
     */
    private String[] allowedOriginPatterns = new String[]{"*"};

    /**
     * 异步发送消息的默认超时时间（默认值：-1，无限超时）
     */
    private Long asyncSendTimeout;

    /**
     * 默认会话空闲超时时间（默认值：0，无限超时）
     */
    private Long maxSessionIdleTimeout;

    /**
     * 文本消息的默认最大缓冲区大小（默认值：0，8 * 1024）
     */
    private Integer maxTextMessageBufferSize;

    /**
     * 二进制消息的默认最大缓冲区大小（默认值：0，8 * 1024）
     */
    private Integer maxBinaryMessageBufferSize;

}
