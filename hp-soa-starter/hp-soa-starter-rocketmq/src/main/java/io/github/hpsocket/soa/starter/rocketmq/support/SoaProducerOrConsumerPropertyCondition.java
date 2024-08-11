
package io.github.hpsocket.soa.starter.rocketmq.support;

import org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

public class SoaProducerOrConsumerPropertyCondition extends AnyNestedCondition
{
    public SoaProducerOrConsumerPropertyCondition()
    {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnBean(ProducerBuilder.class)
    static class DefaultMQProducerExistsCondition
    {
    }

    @ConditionalOnBean(SimpleConsumerBuilder.class)
    static class SimpleConsumerExistsCondition
    {
    }
}
