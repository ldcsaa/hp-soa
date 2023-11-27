package io.github.hpsocket.soa.starter.web.cloud.exception;

import org.springframework.http.HttpStatus;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.sentinel.exception.DefaultSentinelBlockExceptionHandler;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

public class CloudSentinelBlockExceptionHandler extends DefaultSentinelBlockExceptionHandler
{    
    @Override
    protected Response<?> createBlockExceptionResponse(ServiceException se)
    {
        if(WebServerHelper.isEntry())
            return super.createBlockExceptionResponse(se);
        
        return TracingHelper.createExceptionResponse(se, HttpStatus.TOO_MANY_REQUESTS);
    }

}
