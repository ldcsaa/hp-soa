
package io.github.hpsocket.soa.starter.web.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagation.FactoryBuilder;
import brave.baggage.BaggagePropagationConfig.SingleBaggageField;
import brave.propagation.B3Propagation;
import brave.sampler.Sampler;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.starter.job.exclusive.config.SoaExclusiveJobConfig;
import io.github.hpsocket.soa.starter.job.xxljob.config.SoaXxlJobConfig;
import io.github.hpsocket.soa.starter.kafka.config.SoaKafkaConfig;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.config.SoaRabbitmqConsumerConfig;
import io.github.hpsocket.soa.starter.sentinel.config.SoaSentinelConfig;
import io.github.hpsocket.soa.starter.task.config.SoaTaskConfig;
import io.github.hpsocket.soa.starter.web.cloud.advice.CloudControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.starter.web.cloud.advice.CloudControllerRequestAdvice;
import io.github.hpsocket.soa.starter.web.cloud.advice.CloudControllerResponseAdvice;
import io.github.hpsocket.soa.starter.web.cloud.aspect.CloudExclusiveJobInspector;
import io.github.hpsocket.soa.starter.web.cloud.aspect.CloudKafkaListenerMdcInspector;
import io.github.hpsocket.soa.starter.web.cloud.aspect.CloudRabbitmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.web.cloud.aspect.CloudScheduledInspector;
import io.github.hpsocket.soa.starter.web.cloud.aspect.CloudXxlJobInspector;
import io.github.hpsocket.soa.starter.web.cloud.exception.CloudSentinelBlockExceptionHandler;
import io.github.hpsocket.soa.starter.web.cloud.filter.CloudMdcFilter;
import io.github.hpsocket.soa.starter.web.config.ControllerAdviceConfig;
import io.github.hpsocket.soa.starter.web.config.WebConfig;

/** <b>HP-SOA Spring Cloud 配置</b> */
@EnableDiscoveryClient
@AutoConfiguration(before = {WebConfig.class, ControllerAdviceConfig.class, SoaSentinelConfig.class})
public class SoaCloudConfig
{
    public static final String httpMdcFilterRegistrationBeanName        = WebConfig.httpMdcFilterRegistrationBeanName;
    public static final String controllerRequestAdviceBeanName          = ControllerAdviceConfig.controllerRequestAdviceBeanName;
    public static final String controllerResponseAdviceBeanName         = ControllerAdviceConfig.controllerResponseAdviceBeanName;
    public static final String controllerGlobalExceptionAdviceBeanName  = ControllerAdviceConfig.controllerGlobalExceptionAdviceBeanName;
    public static final String sentinelBlockExceptionHandlerBeanName    = SoaSentinelConfig.sentinelBlockExceptionHandlerBeanName;
    
    @Bean
    Tracing tracing()
    {
        FactoryBuilder fb = BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY);
        
        for(String key : MdcAttr.TRANSFER_MDC_ALL_KEYS)
            fb.add(SingleBaggageField.remote(BaggageField.create(key)));
        
        return Tracing.newBuilder()
                .sampler(Sampler.ALWAYS_SAMPLE)
                .localIp(AppConfigHolder.getAppAddress())
                .localPort(AppConfigHolder.getAppPort())
                .localServiceName(AppConfigHolder.getAppName())
                .propagationFactory(fb.build()).build();
    }
    
    @Bean(httpMdcFilterRegistrationBeanName)
    public FilterRegistrationBean<CloudMdcFilter> httpMdcFilterRegistration()
    {
        FilterRegistrationBean<CloudMdcFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CloudMdcFilter());
        registration.setName(CloudMdcFilter.DISPLAY_NAME);
        registration.addUrlPatterns(CloudMdcFilter.URL_PATTERNS);
        registration.setOrder(CloudMdcFilter.ORDER);
        registration.setEnabled(true);
        
        return registration;
    }
    
    @Bean(controllerRequestAdviceBeanName)
    CloudControllerRequestAdvice controllerRequestAdvice()
    {
        return new CloudControllerRequestAdvice();
    }
    
    @Bean(controllerResponseAdviceBeanName)
    CloudControllerResponseAdvice controllerResponseAdvice()
    {
        return new CloudControllerResponseAdvice();
    }
    
    @Bean(controllerGlobalExceptionAdviceBeanName)
    CloudControllerGlobalExceptionAdvice controllerGlobalExceptionAdvice()
    {
        return new CloudControllerGlobalExceptionAdvice();
    }
    
    /** 限流处理器 */
    @Bean(sentinelBlockExceptionHandlerBeanName)
    @ConditionalOnMissingBean(name = sentinelBlockExceptionHandlerBeanName)
    @ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled", matchIfMissing = true)
    public BlockExceptionHandler sentinelBlockExceptionHandler()
    {
        return new CloudSentinelBlockExceptionHandler();
    }
    
    @Bean
    @ConditionalOnClass(SoaTaskConfig.class)
    CloudScheduledInspector cloudScheduledInspector()
    {
        return new CloudScheduledInspector();
    }
    
    @Bean
    @ConditionalOnClass(SoaExclusiveJobConfig.class)
    CloudExclusiveJobInspector cloudExclusiveJobInspector()
    {
        return new CloudExclusiveJobInspector();
    }
    
    @Bean
    @ConditionalOnClass(SoaXxlJobConfig.class)
    CloudXxlJobInspector cloudXxlJobInspector()
    {
        return new CloudXxlJobInspector();
    }
    
    @Bean
    @ConditionalOnClass(SoaKafkaConfig.class)
    CloudKafkaListenerMdcInspector cloudKafkaListenerMdcInspector()
    {
        return new CloudKafkaListenerMdcInspector();
    }
    
    @Bean
    @ConditionalOnClass(SoaRabbitmqConsumerConfig.class)
    CloudRabbitmqListenerMdcInspector cloudRabbitmqListenerMdcInspector()
    {
        return new CloudRabbitmqListenerMdcInspector();
    }

}
