package io.github.hpsocket.soa.starter.web.cloud.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hpsocket.soa.framework.web.advice.ControllerResponseAdvice;
import io.github.hpsocket.soa.starter.web.cloud.support.TraceHelper;

/** <b>Spring Cloud HTTP 响应拦截器</b> */
@RestControllerAdvice
public class CloudControllerResponseAdvice extends ControllerResponseAdvice
{
    private static final ThreadLocal<Boolean> entry = new ThreadLocal<>();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        boolean isEntry = TraceHelper.isEntry();
        entry.set(isEntry);
        
        return isEntry ? super.supports(returnType, converterType) : false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response)
    {
        if(entry.get())
            return super.beforeBodyWrite(body, returnType, selectedContentType, selectedConverterType, request, response);
        
        return body;
    }

}
