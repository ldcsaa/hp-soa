package com.github.hpsocket.soa.starter.web.config;

import com.github.hpsocket.soa.framework.web.holder.SpringContextHolder;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SpringContextHolderConfig
{
    @Bean("springContextHolder")
    public SpringContextHolder springContextHolder()
    {
        return new SpringContextHolder();
    }
}
