package io.github.hpsocket.soa.starter.rocketmq.support;

import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;

public interface SoaRocketMQSimpleConsumerListener
{
    void consume(MessageView messageView, RocketMQClientTemplate rocketMQClientTemplate);
}
