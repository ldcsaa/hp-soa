package io.github.hpsocket.demo.rocketmq.template;

import org.apache.rocketmq.client.annotation.ExtProducerResetConfiguration;

import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;

/** 自定义 Producer Template */
@ExtProducerResetConfiguration
(
    //value = FifoPruducerTemplate.BEAN_NAME,
    topic = "${rocketmq-fifo.simple-consumer.topic}"
)
public class FifoPruducerTemplate extends SoaRocketMQClientTemplate
{
    public static final String BEAN_NAME = "fifoPruducerTemplate";
}
