package io.github.hpsocket.demo.rocketmq.listener;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqHelper;
import lombok.extern.slf4j.Slf4j;

/** FIFO 消息 Push Consumer 监听器 */
@Slf4j
@Component
@RocketMQMessageListener
(
    endpoints = "${rocketmq.simple-consumer.endpoints}",
    topic = "${rocketmq-fifo.simple-consumer.topic}",
    tag = "${rocketmq.simple-consumer.tag}",
    consumerGroup = "fifo-push-consumer-group"
)
public class FifoMessageListener2 implements RocketMQListener
{

    @Override
    public ConsumeResult consume(MessageView messageView)
    {
        JSONObject json = RocketmqHelper.getMessageViewBodyAsJsonObject(messageView);

        log.info("Push Consume Fifo Massage: {}", messageView);
        log.info("Push Fifo Massage Body: {}", json);
        
        int val = ThreadLocalRandom.current().nextInt(10);
        ConsumeResult rs = ((val & 0x1) == 0) ? ConsumeResult.SUCCESS : ConsumeResult.FAILURE;
        
        log.info("{} Fifo Massage (internalMessageId: {})", rs.name(), messageView.getMessageId().toString());

        return rs;
    }

}
