package io.github.hpsocket.soa.starter.web.config;

import io.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import io.github.hpsocket.soa.framework.web.advice.ControllerResponseAdvice;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/** <b>HP-SOA Web Controller Advice 配置</b> */
@AutoConfiguration
public class ControllerAdviceConfig
{
    public static final String controllerRequestAdviceBeanName          = "controllerRequestAdvice";
    public static final String controllerResponseAdviceBeanName         = "controllerResponseAdvice";
    public static final String controllerGlobalExceptionAdviceBeanName  = "controllerGlobalExceptionAdvice";
    
    @Bean(controllerRequestAdviceBeanName)
    @ConditionalOnMissingBean(name = controllerRequestAdviceBeanName)
    ControllerRequestAdvice controllerRequestAdvice()
    {
        return new ControllerRequestAdvice();
    }
    
    @Bean(controllerResponseAdviceBeanName)
    @ConditionalOnMissingBean(name = controllerResponseAdviceBeanName)
    ControllerResponseAdvice controllerResponseAdvice()
    {
        return new ControllerResponseAdvice();
    }
    
    @Bean(controllerGlobalExceptionAdviceBeanName)
    @ConditionalOnMissingBean(name = controllerGlobalExceptionAdviceBeanName)
    ControllerGlobalExceptionAdvice controllerGlobalExceptionAdvice()
    {
        return new ControllerGlobalExceptionAdvice();
    }

}
