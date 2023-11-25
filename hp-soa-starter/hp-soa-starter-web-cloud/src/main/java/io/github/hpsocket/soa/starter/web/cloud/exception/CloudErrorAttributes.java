package io.github.hpsocket.soa.starter.web.cloud.exception;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;

public class CloudErrorAttributes extends DefaultErrorAttributes
{
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options)
    {
        Throwable error = super.getError(webRequest);
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        
        if(error instanceof ServiceException e)
        {
            errorAttributes.put("statusCode", e.getStatusCode());
            errorAttributes.put("resultCode", e.getResultCode());
        }
        
        return errorAttributes;
    }
    
}
