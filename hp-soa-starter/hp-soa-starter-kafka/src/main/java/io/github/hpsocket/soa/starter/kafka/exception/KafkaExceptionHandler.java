package io.github.hpsocket.soa.starter.kafka.exception;

/** <b>Kafka 异常处理器接口</b> */
public interface KafkaExceptionHandler
{
    void handleException(Object msg, String messageId, long timestamp, Exception e);
}
