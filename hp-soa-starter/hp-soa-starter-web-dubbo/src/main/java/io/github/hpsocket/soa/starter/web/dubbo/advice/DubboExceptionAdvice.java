package io.github.hpsocket.soa.starter.web.dubbo.advice;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.dubbo.rpc.RpcException;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.model.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

/** <b>Dubbo 全局异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class DubboExceptionAdvice implements Ordered
{
    @Override
    public int getOrder()
    {
        return -200;
    }

    /** {@linkplain RpcException} 异常处理器 */
    @ExceptionHandler({RpcException.class})
    public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, RpcException e)
    {
        Throwable real  = e;
        Throwable cause = null;
        
        do
        {
            cause = real.getCause();
            
            if(cause == null)
                break;
            
            real = cause;
        } while(cause instanceof RpcException);
        
        
        ServiceException se = null;
        
        if(real instanceof TimeoutException)
            se = wrapServiceException(TIMEOUT_EXCEPTION, real);
        else if(real instanceof ExecutionException)
            se = wrapServiceException(INNER_API_CALL_EXCEPTION, real);
        
        if(se == null)
            se = wrapServiceException(GENERAL_EXCEPTION, real);
        
        logServiceException(log, se.getMessage(), se);
        
        return new Response<>(se);
    }

}
