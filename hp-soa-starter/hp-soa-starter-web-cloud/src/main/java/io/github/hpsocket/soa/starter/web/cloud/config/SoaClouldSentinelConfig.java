package io.github.hpsocket.soa.starter.web.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;

import io.github.hpsocket.soa.starter.sentinel.config.SoaSentinelConfig;
import io.github.hpsocket.soa.starter.web.cloud.advice.CloudSentinelExceptionAdvice;
import io.github.hpsocket.soa.starter.web.cloud.exception.CloudSentinelBlockExceptionHandler;

/** <b>HP-SOA Spring Cloud Sentinel 配置</b> */
@AutoConfiguration(before = SoaSentinelConfig.class)
@ConditionalOnClass(SoaSentinelConfig.class)
public class SoaClouldSentinelConfig
{
    public static final String sentinelExceptionAdviceBeanName       = SoaSentinelConfig.sentinelExceptionAdviceBeanName;
    public static final String sentinelBlockExceptionHandlerBeanName = SoaSentinelConfig.sentinelBlockExceptionHandlerBeanName;
    
    @Bean(sentinelExceptionAdviceBeanName)
    @ConditionalOnMissingBean(name = sentinelExceptionAdviceBeanName)
    CloudSentinelExceptionAdvice sentinelExceptionAdvice()
    {
        return new CloudSentinelExceptionAdvice();
    }
    
    /** 限流处理器 */
    @Bean(sentinelBlockExceptionHandlerBeanName)
    @ConditionalOnMissingBean(name = sentinelBlockExceptionHandlerBeanName)
    @ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled", matchIfMissing = true)
    public BlockExceptionHandler sentinelBlockExceptionHandler()
    {
        return new CloudSentinelBlockExceptionHandler();
    }

}
