package io.github.hpsocket.soa.framework.web.advice;

import java.lang.reflect.Type;

import org.aspectj.lang.annotation.Aspect;
import io.github.hpsocket.soa.framework.core.mdc.MdcRunnable;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;

import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import static io.github.hpsocket.soa.framework.web.support.WebServerHelper.*;

/** <b>HTTP 请求拦截器</b> */
@Slf4j
@Aspect
@RestControllerAdvice
public class ControllerRequestAdvice extends RequestBodyAdviceAdapter implements Ordered
{
    @Override
    public int getOrder()
    {
        return -10;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {

    }
    
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        RequestContext.setBody(body);
        
        if(WebServerHelper.isEntryOrNull())
            logRequestBody();
        
        return body;
    }

    private void logRequestBody()
    {
        try
        {
            final RequestAttribute requestAttribute = RequestContext.getRequestAttribute();
            
            ASYNC_LOG_EXECUTOR.execute(new MdcRunnable()
            {    
                @Override
                protected void doRun()
                {
                    String strBody = GeneralHelper.truncateAndMore(JSONObject.toJSONString(requestAttribute.getBody(), JSON_SERIAL_FEATURES_NO_NULL_VAL), REQ_BODY_MAX_LOG_LENGTH);
                    log.info("[ REQUEST BODY ] -> {}", strBody);
                }
            });
        }
        catch(Exception e)
        {
            log.error("async write request body log fail", e);
        }
    }

}
