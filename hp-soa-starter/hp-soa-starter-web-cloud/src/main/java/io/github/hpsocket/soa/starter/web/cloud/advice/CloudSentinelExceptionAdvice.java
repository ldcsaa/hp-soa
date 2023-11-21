package io.github.hpsocket.soa.starter.web.cloud.advice;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.csp.sentinel.slots.block.BlockException;

import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.starter.sentinel.advice.SentinelExceptionAdvice;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>Sentinel 全局异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class CloudSentinelExceptionAdvice extends SentinelExceptionAdvice
{
    /** {@linkplain BlockException} 异常处理器 */
    @Override
    @ExceptionHandler({BlockException.class})
    public Response<?> handleBlockException(HttpServletRequest request, HttpServletResponse response, BlockException e) throws BlockException
    {
        if(TracingHelper.isEntry())
            return super.handleBlockException(request, response, e);
        
        log.error(e.getMessage(), e);
        
        throw e;
    }

    /** {@linkplain UndeclaredThrowableException} 异常处理器 */
    @Override
    @ExceptionHandler({UndeclaredThrowableException.class})
    public Response<?> handleUndeclaredThrowableException(HttpServletRequest request, HttpServletResponse response, UndeclaredThrowableException e) throws UndeclaredThrowableException, Exception
    {
        if(TracingHelper.isEntry())
            return super.handleUndeclaredThrowableException(request, response, e);
        
        log.error(e.getMessage(), e);
        
        throw e;
    }
    
    /** {@linkplain RuntimeException} 异常处理器 */
    @Override
    @ExceptionHandler({RuntimeException.class})
    public Response<?> handleRuntimeException(HttpServletRequest request, HttpServletResponse response, RuntimeException e) throws Exception
    {
        if(TracingHelper.isEntry())
            return super.handleRuntimeException(request, response, e);
        
        log.error(e.getMessage(), e);
        
        throw e;
    }

}
