package io.github.hpsocket.demo.rocketmq.template;

import org.apache.rocketmq.client.annotation.ExtProducerResetConfiguration;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;

@ExtProducerResetConfiguration(value = "fifoPruducerTemplate", topic = "${rocketmq-fifo.producer.topic:}")
public class FifoPruducerTemplate extends RocketMQClientTemplate
{

}
