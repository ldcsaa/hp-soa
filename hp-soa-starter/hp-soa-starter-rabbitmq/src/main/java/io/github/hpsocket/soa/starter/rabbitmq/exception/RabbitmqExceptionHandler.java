package io.github.hpsocket.soa.starter.rabbitmq.exception;

/** <b>RabbitMQ 异常处理器接口</b> */
public interface RabbitmqExceptionHandler
{
    void handleException(Object msg, String messageId, long timestamp, Exception e);
}
