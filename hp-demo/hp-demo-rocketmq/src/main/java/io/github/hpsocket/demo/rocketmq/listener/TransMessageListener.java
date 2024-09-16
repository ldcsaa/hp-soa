package io.github.hpsocket.demo.rocketmq.listener;

import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqHelper;
import lombok.extern.slf4j.Slf4j;

/** 事务消息 Push Consumer 监听器 */
@Slf4j
@Component
@RocketMQMessageListener
(
    endpoints = "${rocketmq-trans.push-consumer.endpoints}",
    topic = "${rocketmq-trans.push-consumer.topic}", 
    tag = "${rocketmq-trans.push-consumer.tag}",
    consumerGroup = "${rocketmq-trans.push-consumer.consumer-group}"
)
public class TransMessageListener implements RocketMQListener
{

    @Override
    public ConsumeResult consume(MessageView messageView)
    {
        JSONObject json = RocketmqHelper.getMessageViewBodyAsJsonObject(messageView);

        log.info("Consume Trans Massage: {}", messageView);
        log.info("Trans Massage Body: {}", json);
        
        return ConsumeResult.SUCCESS;
    }

}
