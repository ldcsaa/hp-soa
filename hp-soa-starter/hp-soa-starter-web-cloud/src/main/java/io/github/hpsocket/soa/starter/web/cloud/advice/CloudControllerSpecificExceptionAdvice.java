package io.github.hpsocket.soa.starter.web.cloud.advice;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import feign.FeignException.FeignServerException;
import feign.RetryableException;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

/** <b>Spring Cloud HTTP 请求全局异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class CloudControllerSpecificExceptionAdvice implements Ordered
{
    public static final int ORDER = ControllerGlobalExceptionAdvice.ORDER - 200;
    
    /** {@linkplain FeignException} 异常处理器 */
    @ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler({FeignException.class})
    public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, FeignException e)
    {
        if(WebServerHelper.isEntry())
        {
            ServiceException se = null;
            
            if(e instanceof RetryableException || e instanceof FeignServerException.GatewayTimeout)
                se = new ServiceException("服务调用超时", INNER_API_CALL_FAIL, e);
            else
                se = wrapServiceException(INNER_API_CALL_EXCEPTION, e);
            
            logServiceException(log, se.getMessage(), se);
            
            return new Response<>(se);
        }
        else
        {
            log.error(e.getMessage(), e);
            return TracingHelper.createExceptionResponse(e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Override
    public int getOrder()
    {
        return ORDER;
    }

}
