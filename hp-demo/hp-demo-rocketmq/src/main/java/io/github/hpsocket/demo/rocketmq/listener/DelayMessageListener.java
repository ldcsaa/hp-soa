package io.github.hpsocket.demo.rocketmq.listener;

import io.github.hpsocket.soa.framework.web.service.AsyncService;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqHelper;
import lombok.extern.slf4j.Slf4j;

/** Delay 消息 Push Consumer 监听器 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RocketMQMessageListener
(
    endpoints = "${rocketmq-delay.push-consumer.endpoints}",
    topic = "xj-social-red-packet-expire",
    consumerGroup = "${rocketmq-delay.push-consumer.consumer-group}"
)
public class DelayMessageListener implements RocketMQListener
{
    private final AsyncService asyncService;

    @Override
    public ConsumeResult consume(MessageView messageView)
    {
        JSONObject json = RocketmqHelper.getMessageViewBodyAsJsonObject(messageView);

        log.info("Consume Delay Massage: {}", messageView);
        log.info("Delay Massage Body: {}", json);
        
        return ConsumeResult.SUCCESS;
    }

}
