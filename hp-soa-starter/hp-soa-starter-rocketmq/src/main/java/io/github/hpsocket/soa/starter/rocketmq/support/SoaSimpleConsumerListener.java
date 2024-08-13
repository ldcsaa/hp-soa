package io.github.hpsocket.soa.starter.rocketmq.support;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;

public interface SoaSimpleConsumerListener
{
    void consume(MessageView messageView, SoaRocketMQClientTemplate rocketMQClientTemplate) throws ClientException;
}
