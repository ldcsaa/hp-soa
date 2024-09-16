
package io.github.hpsocket.soa.starter.web.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.web.cloud.advice.CloudControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.starter.web.cloud.advice.CloudControllerSpecificExceptionAdvice;
import io.github.hpsocket.soa.starter.web.cloud.exception.CloudErrorAttributes;
import io.github.hpsocket.soa.starter.web.cloud.exception.CloudErrorDecoder;
import io.github.hpsocket.soa.starter.web.cloud.filter.CloudMdcFilter;
import io.github.hpsocket.soa.starter.web.cloud.interceptor.FeignTracingInterceptor;
import io.github.hpsocket.soa.starter.web.config.ControllerAdviceConfig;
import io.github.hpsocket.soa.starter.web.config.WebConfig;

/** <b>HP-SOA Spring Cloud 基本配置</b> */
@EnableDiscoveryClient
@AutoConfiguration(before = {WebConfig.class, ControllerAdviceConfig.class})
public class SoaCloudConfig
{
    public static final String httpMdcFilterRegistrationBeanName                = WebConfig.httpMdcFilterRegistrationBeanName;
    
    public static final String controllerGlobalExceptionAdviceBeanName          = ControllerAdviceConfig.controllerGlobalExceptionAdviceBeanName;
    public static final String cloudControllerSpecificExceptionAdviceBeanName   = "cloudControllerSpecificException";
    
    @Bean
    FeignTracingInterceptor feignTracingInterceptor()
    {
        return new FeignTracingInterceptor();
    }
    
    @Bean
    CloudErrorDecoder cloudErrorDecoder()
    {
        return new CloudErrorDecoder();
    }
    
    @Bean
    CloudErrorAttributes cloudErrorAttributes()
    {
        return new CloudErrorAttributes();
    }
    
    @Bean(httpMdcFilterRegistrationBeanName)
    FilterRegistrationBean<CloudMdcFilter> httpMdcFilterRegistration()
    {
        FilterRegistrationBean<CloudMdcFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CloudMdcFilter());
        registration.setName(CloudMdcFilter.DISPLAY_NAME);
        registration.addUrlPatterns(CloudMdcFilter.URL_PATTERNS);
        registration.setOrder(CloudMdcFilter.ORDER);
        registration.setEnabled(true);
        
        return registration;
    }
    
    @Bean(controllerGlobalExceptionAdviceBeanName)
    CloudControllerGlobalExceptionAdvice controllerGlobalExceptionAdvice()
    {
        return new CloudControllerGlobalExceptionAdvice();
    }
    
    @Bean(cloudControllerSpecificExceptionAdviceBeanName)
    CloudControllerSpecificExceptionAdvice cloudControllerSpecificExceptionAdvice()
    {
        return new CloudControllerSpecificExceptionAdvice();
    }
    
}
