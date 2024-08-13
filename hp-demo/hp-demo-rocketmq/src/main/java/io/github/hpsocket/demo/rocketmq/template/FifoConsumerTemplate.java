package io.github.hpsocket.demo.rocketmq.template;

import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaExtSimpleConsumerResetConfiguration;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;

@SoaExtSimpleConsumerResetConfiguration
(
    value = FifoConsumerTemplate.BEAN_NAME,
    autoStart = "${rocketmq-fifo.simple-consumer.receive.auto-start}",
    consumptionThreadCount = "${rocketmq-fifo.simple-consumer.receive.consumption-thread-count}",
    maxMessageNum = "${rocketmq-fifo.simple-consumer.receive.max-message-num}",
    invisibleDuration = "${rocketmq-fifo.simple-consumer.invisible-duration}",
    topic = "${rocketmq-fifo.simple-consumer.topic}"
)
public class FifoConsumerTemplate extends SoaRocketMQClientTemplate
{
    public static final String BEAN_NAME = "fifoConsumerTemplate";
}
