package io.github.hpsocket.soa.starter.web.cloud.advice;

import java.lang.reflect.Type;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import io.github.hpsocket.soa.starter.web.cloud.support.TraceHelper;

/** <b>Spring Cloud HTTP 请求拦截器</b> */
@Aspect
@RestControllerAdvice
public class CloudControllerRequestAdvice extends ControllerRequestAdvice
{
    private static final ThreadLocal<Boolean> entry = new ThreadLocal<>();
    
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        boolean isEntry = TraceHelper.isEntry();
        entry.set(isEntry);
        
        return isEntry ? super.supports(methodParameter, targetType, converterType) : false;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        if(entry.get())
            return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
        
        return body;
    }

}
