package org.hpsocket.soa.starter.web.config;

import org.hpsocket.soa.framework.web.holder.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContextHolderConfig
{
    @Bean
    public SpringContextHolder springContextHolder()
    {
        return new SpringContextHolder();
    }
}
