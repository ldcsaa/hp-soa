package io.github.hpsocket.demo.rocketmq.listener;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;

import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleMessageListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaSimpleConsumerListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SoaSimpleMessageListener(autoAck = true)
public class NormalMessageListener implements SoaSimpleConsumerListener
{

    @Override
    public void consume(MessageView messageView, SoaRocketMQClientTemplate rocketMQClientTemplate) throws ClientException
    {
        log.info("Consumer Normal Massage: {}", messageView);
    }

}
