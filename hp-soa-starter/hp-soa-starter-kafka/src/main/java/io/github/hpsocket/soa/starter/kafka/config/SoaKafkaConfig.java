
package io.github.hpsocket.soa.starter.kafka.config;

import io.github.hpsocket.soa.starter.kafka.consumer.aspect.KafkaListenerMdcInspector;
import io.github.hpsocket.soa.starter.kafka.consumer.aspect.KafkaListenerTracingInspector;
import io.github.hpsocket.soa.starter.kafka.consumer.listener.KafkaReadOnlyEventListener;
import io.github.hpsocket.soa.starter.kafka.producer.interceptor.ProducerMdcInterceptor;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;

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

}
