
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerTracingInspector;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqReadOnlyEventListener;

/** <b>HP-SOA Rabbitmq Consumer 配置</b> */
@EnableRabbit
@AutoConfiguration
public class SoaRabbitmqConsumerConfig
{
    @Bean
    RabbitmqReadOnlyEventListener rabbitmqReadOnlyEventListener()
    {
        return new RabbitmqReadOnlyEventListener();
    }
    
    @Bean
    @ConditionalOnClass(Trace.class)
    RabbitmqListenerTracingInspector rabbitmqListenerTracingInspector()
    {
        return new RabbitmqListenerTracingInspector();
    }    
    
    @Bean
    RabbitmqListenerMdcInspector rabbitmqListenerMdcInspector()
    {
        return new RabbitmqListenerMdcInspector();
    }
    
}
