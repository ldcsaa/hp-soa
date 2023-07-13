package com.github.hpsocket.soa.starter.web.config;

import com.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContextHolderConfig
{
    @Bean(name="springContextHolder")
    public SpringContextHolder springContextHolder()
    {
        return new SpringContextHolder();
    }
}
