package io.github.hpsocket.demo.rocketmq.listener;

import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RocketMQMessageListener
(
    endpoints = "${rocketmq-delay.push-consumer.endpoints}",
    topic = "${rocketmq-delay.push-consumer.topic}", 
    tag = "${rocketmq-delay.push-consumer.tag}",
    consumerGroup = "${rocketmq-delay.push-consumer.consumer-group}"
)
public class DelayMessageListener implements RocketMQListener
{

    @Override
    public ConsumeResult consume(MessageView messageView)
    {
        log.info("Consumer Delay Massage: {}", messageView);
        
        return ConsumeResult.SUCCESS;
    }

}
