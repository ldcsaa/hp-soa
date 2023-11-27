package io.github.hpsocket.soa.starter.web.cloud.advice;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.csp.sentinel.slots.block.BlockException;

import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.sentinel.advice.SentinelExceptionAdvice;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>Spring Cloud Sentinel 异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class CloudSentinelExceptionAdvice extends SentinelExceptionAdvice
{
    /** {@linkplain BlockException} 异常处理器 */
    @Override
    @ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler({BlockException.class})
    public Response<?> handleBlockException(HttpServletRequest request, HttpServletResponse response, BlockException e)
    {
        if(WebServerHelper.isEntry())
            return super.handleBlockException(request, response, e);
        else
        {
            log.error(e.getMessage());
            return TracingHelper.createExceptionResponse(e, HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    /** {@linkplain UndeclaredThrowableException} 异常处理器 */
    @Override
    @ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler({UndeclaredThrowableException.class})
    public Response<?> handleUndeclaredThrowableException(HttpServletRequest request, HttpServletResponse response, UndeclaredThrowableException e)
    {
        if(WebServerHelper.isEntry())
            return super.handleUndeclaredThrowableException(request, response, e);
        else
        {
            log.error(e.getMessage(), e);
            return TracingHelper.createExceptionResponse(e, HttpStatus.EXPECTATION_FAILED);
        }
    }
    
    /** {@linkplain RuntimeException} 异常处理器 */
    @Override
    @ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler({RuntimeException.class})
    public Response<?> handleRuntimeException(HttpServletRequest request, HttpServletResponse response, RuntimeException e)
    {
        if(WebServerHelper.isEntry())
            return super.handleRuntimeException(request, response, e);
        else
        {
            log.error(e.getMessage(), e);
            return TracingHelper.createExceptionResponse(e, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
