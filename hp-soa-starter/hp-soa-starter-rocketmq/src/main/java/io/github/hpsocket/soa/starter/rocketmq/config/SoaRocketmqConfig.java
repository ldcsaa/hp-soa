
package io.github.hpsocket.soa.starter.rocketmq.config;

import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.aspect.RocketmqListenerTracingInspector;
import io.github.hpsocket.soa.starter.rocketmq.consumer.listener.RocketmqReadOnlyEventListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaSimpleConsumerReceiveProperties;
import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

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
    @ConditionalOnExpression("'${rocketmq.producer.endpoints:}' != '' || '${rocketmq.simple-consumer.endpoints:}' != ''")
    @Bean(name = ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME, destroyMethod = "destroy")
    SoaRocketMQClientTemplate rocketMQClientTemplate()
    {
        return new SoaRocketMQClientTemplate();
    }
    
}
