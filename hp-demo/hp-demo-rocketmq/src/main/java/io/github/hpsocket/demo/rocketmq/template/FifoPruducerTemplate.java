package io.github.hpsocket.demo.rocketmq.template;

import org.apache.rocketmq.client.annotation.ExtProducerResetConfiguration;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;

@ExtProducerResetConfiguration(value = FifoPruducerTemplate.BEAN_NAME, topic = "${rocketmq-fifo.simple-consumer.topic}")
public class FifoPruducerTemplate extends RocketMQClientTemplate
{
    public static final String BEAN_NAME = "fifoPruducerTemplate";
}
