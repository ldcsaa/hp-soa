package io.github.hpsocket.soa.starter.web.cloud.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hpsocket.soa.framework.web.advice.ControllerResponseAdvice;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

/** <b>Spring Cloud HTTP 响应拦截器</b> */
@RestControllerAdvice
public class CloudControllerResponseAdvice extends ControllerResponseAdvice
{
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        return super.supports(returnType, converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response)
    {
        Boolean isEntry = TracingHelper.isEntryOrNull();
        
        if(Boolean.TRUE.equals(isEntry))
            return super.beforeBodyWrite(body, returnType, selectedContentType, selectedConverterType, request, response);
        
        if(body instanceof Response<?> respBody)
            respBody.setCostTime(WebServerHelper.calcTimestamp());
        
        return body;
    }

}
