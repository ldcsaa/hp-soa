package io.github.hpsocket.demo.rocketmq.template;

import org.apache.rocketmq.client.annotation.ExtConsumerResetConfiguration;

import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleConsumerReceiveConfiguration;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;

/** Simple Consumer Template */
@ExtConsumerResetConfiguration
(
    //value = FifoConsumerTemplate.BEAN_NAME,
    topic = "${rocketmq-fifo.simple-consumer.topic}",
    consumerGroup = "${rocketmq-fifo.simple-consumer.consumer-group}"
)
@SoaSimpleConsumerReceiveConfiguration
(
    autoStart = "${rocketmq-fifo.simple-consumer.receive.auto-start}",
    consumptionThreadCount = "${rocketmq-fifo.simple-consumer.receive.consumption-thread-count}",
    maxMessageNum = "${rocketmq-fifo.simple-consumer.receive.max-message-num}",
    invisibleDuration = "${rocketmq-fifo.simple-consumer.receive.invisible-duration}"
)
public class FifoConsumerTemplate extends SoaRocketMQClientTemplate
{
    public static final String BEAN_NAME = "fifoConsumerTemplate";
}
