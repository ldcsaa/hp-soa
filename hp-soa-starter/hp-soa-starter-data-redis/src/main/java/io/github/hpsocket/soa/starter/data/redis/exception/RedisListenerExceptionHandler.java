package io.github.hpsocket.soa.starter.data.redis.exception;

/** <b>RabbitMQ 异常处理器接口</b> */
public interface RedisListenerExceptionHandler
{
    void handleException(CharSequence channel, Object msg, long timestamp, Exception e);
}
