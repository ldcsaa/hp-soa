package io.github.hpsocket.soa.starter.rocketmq.exception;

import org.apache.rocketmq.client.apis.message.MessageView;

/** <b>RocketMQ 异常处理器接口</b> */
public interface RocketmqExceptionHandler
{
    void handleException(MessageView msg, String messageId, long timestamp, Exception e);
}
