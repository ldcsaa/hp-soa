package io.github.hpsocket.soa.starter.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;

/** <b>HP-SOA Web 上下文配置</b> */
@AutoConfiguration
public class ContextConfig
{
    public static final String springContextHolderBeanName = "springContextHolder";
    
    /** {@linkplain SpringContextHolder} Spring 上下文持有者配置 */
    @Bean(springContextHolderBeanName)
    public SpringContextHolder springContextHolder(ApplicationContext applicationContext)
    {
        return new SpringContextHolder(applicationContext);
    }
    
}
