
package io.github.hpsocket.soa.starter.rocketmq.config;

import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerTracingInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.listener.RocketmqReadOnlyEventListener;

/** <b>HP-SOA Rocketmq 配置</b> */
@AutoConfiguration(before = {RocketMQAutoConfiguration.class})
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
    
    @Bean
    @ConditionalOnMissingBean(ProducerInterceptor.class)
    ProducerInterceptor<Object, Object> producerMdcInterceptor()
    {
        return new ProducerMdcInterceptor<Object, Object>();
    }

    /** 默认 Kafka Template，在事务环境下支持以非事方式务发送消息。 */
    @Bean
    @ConditionalOnMissingBean(KafkaTemplate.class)
    KafkaTemplate<?, ?> allowNonTransactionalKafkaTemplate(
        KafkaProperties properties,
        ProducerFactory<Object, Object> kafkaProducerFactory,
        ProducerListener<Object, Object> kafkaProducerListener,
        ObjectProvider<RecordMessageConverter> messageConverter, 
        ObjectProvider<ProducerInterceptor<Object, Object>> producerInterceptor)
    {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        producerInterceptor.ifUnique(kafkaTemplate::setProducerInterceptor);
        map.from(kafkaProducerListener).to(kafkaTemplate::setProducerListener);
        map.from(properties.getTemplate().getDefaultTopic()).to(kafkaTemplate::setDefaultTopic);
        map.from(properties.getTemplate().getTransactionIdPrefix()).to(kafkaTemplate::setTransactionIdPrefix);
        kafkaTemplate.setAllowNonTransactional(true);
        
        return kafkaTemplate;
    }
    
}
