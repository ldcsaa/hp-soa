package io.github.hpsocket.demo.rocketmq.listener;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.demo.rocketmq.template.FifoConsumerTemplate;
import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleMessageListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaSimpleConsumerListener;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqHelper;
import lombok.extern.slf4j.Slf4j;

/** FIFO 消息 Simple Consumer 监听器 */
@Slf4j
@SoaSimpleMessageListener(autoAck = false, rocketMQTemplateBeanName = FifoConsumerTemplate.BEAN_NAME)
public class FifoMessageListener implements SoaSimpleConsumerListener
{

    @Override
    public void consume(MessageView messageView, SoaRocketMQClientTemplate rocketMQClientTemplate) throws ClientException
    {
        JSONObject json = RocketmqHelper.getMessageViewBodyAsJsonObject(messageView);

        log.info("Consume Fifo Massage: {}", messageView);
        log.info("Fifo Massage Body: {}", json);
        
        int val = ThreadLocalRandom.current().nextInt(10);
        boolean ack = ((val & 0x1) == 0);
        
        log.info("{} Fifo Massage (internalMessageId: {})", ack ? "ACK" : "UNACK", messageView.getMessageId().toString());
        
        if(ack) rocketMQClientTemplate.ack(messageView);
    }

}
