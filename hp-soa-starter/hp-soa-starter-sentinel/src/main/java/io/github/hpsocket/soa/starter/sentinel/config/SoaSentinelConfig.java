
package io.github.hpsocket.soa.starter.sentinel.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.starter.sentinel.advice.SentinelExceptionAdvice;
import io.github.hpsocket.soa.starter.sentinel.exception.DefaultSentinelBlockExceptionHandler;

/** <b>HP-SOA Sentinel 配置</b> */
@AutoConfiguration
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
public class SoaSentinelConfig
{
    public static final String sentinelExceptionAdviceBeanName       = "sentinelExceptionAdvice";
    public static final String sentinelBlockExceptionHandlerBeanName = "sentinelBblockExceptionHandler";
    
    @Bean(sentinelExceptionAdviceBeanName)
    @ConditionalOnMissingBean(name = sentinelExceptionAdviceBeanName)
    SentinelExceptionAdvice sentinelExceptionAdvice()
    {
        return new SentinelExceptionAdvice();
    }

    /** 默认限流处理器 */
    @Bean(sentinelBlockExceptionHandlerBeanName)
    @ConditionalOnMissingBean(name = sentinelBlockExceptionHandlerBeanName)
    @ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled", matchIfMissing = true)
    BlockExceptionHandler sentinelBlockExceptionHandler()
    {
        return new DefaultSentinelBlockExceptionHandler();
    }

    /** 资源变换器 */
    @Bean
    @ConditionalOnMissingBean(UrlCleaner.class)
    @ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled", matchIfMissing = true)
    UrlCleaner urlCleaner()
    {
        final Set<String> suffixSet = new HashSet<>(Arrays.asList(".js", ".css", ".html", ".ico", ".txt", ".md", ".jpg", ".png"));
        
        return new UrlCleaner()
        {
            @Override
            public String clean(String originUrl)
            {
                if(GeneralHelper.isStrEmpty(originUrl))
                    return originUrl;
                
                int i = originUrl.lastIndexOf('.');
                
                if(i < 0)
                    return originUrl;
                
                String suffix = originUrl.substring(i).toLowerCase();
                
                if(suffixSet.contains(suffix))
                    return null;
                
                return originUrl;
            }
        };
    }
    
}
