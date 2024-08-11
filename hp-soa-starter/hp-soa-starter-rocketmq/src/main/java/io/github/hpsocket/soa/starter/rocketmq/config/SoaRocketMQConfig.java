
package io.github.hpsocket.soa.starter.rocketmq.config;

import org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.apache.rocketmq.client.support.RocketMQMessageConverter;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.context.annotation.Primary;

import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerTracingInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.listener.RocketmqReadOnlyEventListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaProducerOrConsumerPropertyCondition;

import static org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration.*;

/** <b>HP-SOA Rocketmq 配置</b> */
@Import({SoaRocketMQSimpleConsumerConfig.class})
@AutoConfiguration(before = {RocketMQAutoConfiguration.class})
public class SoaRocketMQConfig
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
    

    /** 默认 Rocketmq Template */
    @Primary
    @DependsOn(SpringContextHolder.springContextHolderBeanName)
    @Conditional(SoaProducerOrConsumerPropertyCondition.class)
    @ConditionalOnMissingBean(name = ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
    @Bean(name = ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME, destroyMethod = "destroy")
    public RocketMQClientTemplate rocketMQClientTemplate(RocketMQMessageConverter rocketMQMessageConverter) {
        RocketMQClientTemplate rocketMQClientTemplate = new SoaRocketMQClientTemplate();

        if (SpringContextHolder.getApplicationContext().containsBean(PRODUCER_BUILDER_BEAN_NAME)) {
            rocketMQClientTemplate.setProducerBuilder((ProducerBuilder) SpringContextHolder.getBean(PRODUCER_BUILDER_BEAN_NAME));
        }
        if (SpringContextHolder.getApplicationContext().containsBean(SIMPLE_CONSUMER_BUILDER_BEAN_NAME)) {
            rocketMQClientTemplate.setSimpleConsumerBuilder((SimpleConsumerBuilder) SpringContextHolder.getBean(SIMPLE_CONSUMER_BUILDER_BEAN_NAME));
        }
        rocketMQClientTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        return rocketMQClientTemplate;
    }
    
}
