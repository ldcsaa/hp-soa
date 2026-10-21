
package io.github.hpsocket.soa.starter.kafka.config;

import io.github.hpsocket.soa.starter.kafka.consumer.aspect.KafkaListenerMdcInspector;
import io.github.hpsocket.soa.starter.kafka.consumer.aspect.KafkaListenerTracingInspector;
import io.github.hpsocket.soa.starter.kafka.consumer.listener.KafkaReadOnlyEventListener;
import io.github.hpsocket.soa.starter.kafka.producer.interceptor.ProducerMdcInterceptor;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.micrometer.KafkaTemplateObservationConvention;

/** <b>HP-SOA Kafka 配置</b> */
@AutoConfiguration(before = {KafkaAutoConfiguration.class})
@MapperScan(basePackages = "io.github.hpsocket.soa.starter.kafka.producer.mapper")
public class SoaKafkaConfig
{
    @Bean
    KafkaReadOnlyEventListener kafkaReadOnlyEventListener()
    {
        return new KafkaReadOnlyEventListener();
    }
    
    @Bean
    @ConditionalOnClass(Trace.class)
    KafkaListenerTracingInspector kafkaListenerTracingInspector()
    {
        return new KafkaListenerTracingInspector();
    }    
    
    @Bean
    KafkaListenerMdcInspector kafkaListenerMdcInspector()
    {
        return new KafkaListenerMdcInspector();
    }
    
    @Bean
    @ConditionalOnMissingBean(ProducerInterceptor.class)
    ProducerInterceptor<Object, Object> producerMdcInterceptor()
    {
        return new ProducerMdcInterceptor<>();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(KafkaTemplate.class)
    KafkaTemplate<?, ?> kafkaTemplate(
                            KafkaProperties properties,
                            ProducerFactory<Object, Object> kafkaProducerFactory,
                            ProducerListener<Object, Object> kafkaProducerListener,
                            ObjectProvider<RecordMessageConverter> messageConverter,
                            ObjectProvider<KafkaTemplateObservationConvention> observationConvention,
                            ObjectProvider<ProducerInterceptor<Object, Object>> producerInterceptor)
    {
        PropertyMapper map = PropertyMapper.get();
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);

        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        observationConvention.ifUnique(kafkaTemplate::setObservationConvention);
        producerInterceptor.ifUnique(kafkaTemplate::setProducerInterceptor);
        map.from(kafkaProducerListener).to(kafkaTemplate::setProducerListener);

        KafkaProperties.Template templateProperties = properties.getTemplate();
        map.from(templateProperties.getDefaultTopic()).to(kafkaTemplate::setDefaultTopic);
        map.from(templateProperties.getTransactionIdPrefix()).to(kafkaTemplate::setTransactionIdPrefix);
        map.from(templateProperties.getCloseTimeout()).to(kafkaTemplate::setCloseTimeout);
        map.from(templateProperties.isAllowNonTransactional()).to(kafkaTemplate::setAllowNonTransactional);
        map.from(templateProperties.isObservationEnabled()).to(kafkaTemplate::setObservationEnabled);

        return kafkaTemplate;
    }
}
