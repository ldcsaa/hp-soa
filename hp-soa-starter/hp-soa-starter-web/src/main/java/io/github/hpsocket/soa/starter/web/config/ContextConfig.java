package io.github.hpsocket.soa.starter.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.web.properties.SecurityProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties;

/** <b>HP-SOA Web 上下文配置</b> */
@AutoConfiguration
public class ContextConfig
{
    public static final String springContextHolderBeanName = "springContextHolder";
    
    private static final String SERVER_PORT_KEY = "server.port";
    
    /** {@linkplain SpringContextHolder} Spring 上下文持有者配置 */
    @Bean(springContextHolderBeanName)
    public SpringContextHolder springContextHolder(ApplicationContext applicationContext, WebProperties webProperties, SecurityProperties securityProperties)
    {
        int serverPort = Integer.valueOf(applicationContext.getEnvironment().getProperty(SERVER_PORT_KEY));
        AppConfigHolder.init(webProperties, securityProperties, serverPort);
        
        return new SpringContextHolder(applicationContext);
    }
    
}
