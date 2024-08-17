package io.github.hpsocket.demo.rocketmq.listener;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleMessageListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaSimpleConsumerListener;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqHelper;
import lombok.extern.slf4j.Slf4j;

/** 普通消息 Simple Consumer 监听器 */
@Slf4j
@SoaSimpleMessageListener(autoAck = true)
public class NormalMessageListener implements SoaSimpleConsumerListener
{

    @Override
    public void consume(MessageView messageView, SoaRocketMQClientTemplate rocketMQClientTemplate) throws ClientException
    {
        JSONObject json = RocketmqHelper.getMessageViewBodyAsJsonObject(messageView);

        log.info("Consume Normal Massage: {}", messageView);
        log.info("Normal Massage Body: {}", json);
    }

}
