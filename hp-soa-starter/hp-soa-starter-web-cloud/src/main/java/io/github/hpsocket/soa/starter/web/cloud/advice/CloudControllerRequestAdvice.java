package io.github.hpsocket.soa.starter.web.cloud.advice;

import java.lang.reflect.Type;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

/** <b>Spring Cloud HTTP 请求拦截器</b> */
@Aspect
@RestControllerAdvice
public class CloudControllerRequestAdvice extends ControllerRequestAdvice
{
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        return super.supports(methodParameter, targetType, converterType);
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        Boolean isEntry = TracingHelper.isEntryOrNull();
        return Boolean.TRUE.equals(isEntry) ? super.afterBodyRead(body, inputMessage, parameter, targetType, converterType) : body;
    }

}
