
package io.github.hpsocket.soa.starter.rocketmq.config;

import org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.client.support.RocketMQMessageConverter;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerTracingInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.listener.RocketmqReadOnlyEventListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaSimpleConsumerReceiveProperties;

import static org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration.*;

/** <b>HP-SOA RocketMQ 配置</b> */
@Import({SoaSimpleConsumerConfig.class})
@AutoConfiguration(before = {RocketMQAutoConfiguration.class})
@EnableConfigurationProperties(SoaSimpleConsumerReceiveProperties.class)
public class SoaRocketmqConfig
{
    @Bean
    RocketmqReadOnlyEventListener rocketmqReadOnlyEventListener()
    {
        return new RocketmqReadOnlyEventListener();
    }
    
    @Bean
    @ConditionalOnClass(Trace.class)
    RocketmqListenerTracingInspector rocketmqListenerTracingInspector()
    {
        return new RocketmqListenerTracingInspector();
    }    
    
    @Bean
    RocketmqListenerMdcInspector rocketmqListenerMdcInspector()
    {
        return new RocketmqListenerMdcInspector();
    }
    
    /** 默认 RocketMQ Client Template */
    @Primary
    @ConditionalOnMissingBean(name = ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
    @Bean(name = ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME, destroyMethod = "destroy")
    SoaRocketMQClientTemplate rocketMQClientTemplate
    (
        RocketMQMessageConverter rocketMQMessageConverter,
        @Qualifier(PRODUCER_BUILDER_BEAN_NAME) ObjectProvider<ProducerBuilder> producerBuilder,
        @Qualifier(SIMPLE_CONSUMER_BUILDER_BEAN_NAME) ObjectProvider<SimpleConsumerBuilder> simpleConsumerBuilder
    )
    {
        SoaRocketMQClientTemplate rocketMQClientTemplate = new SoaRocketMQClientTemplate();
        
        producerBuilder.ifUnique(rocketMQClientTemplate::setProducerBuilder);
        simpleConsumerBuilder.ifUnique(rocketMQClientTemplate::setSimpleConsumerBuilder);

        if(rocketMQClientTemplate.getProducerBuilder() == null && rocketMQClientTemplate.getSimpleConsumerBuilder() == null)
            throw new IllegalStateException(String.format("no bean named '%s' or '%s' for target bean '%s' to inject", 
                                            PRODUCER_BUILDER_BEAN_NAME, SIMPLE_CONSUMER_BUILDER_BEAN_NAME, ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME));
        
        rocketMQClientTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        
        return rocketMQClientTemplate;
    }
    
}
