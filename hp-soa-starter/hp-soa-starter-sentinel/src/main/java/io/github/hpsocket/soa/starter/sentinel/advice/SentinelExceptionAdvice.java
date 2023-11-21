package io.github.hpsocket.soa.starter.sentinel.advice;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.framework.web.model.Response;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>Sentinel 全局异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class SentinelExceptionAdvice implements Ordered
{
    public static final int ORDER = ControllerGlobalExceptionAdvice.ORDER - 100;
    
    @Autowired
    private ControllerGlobalExceptionAdvice globalExceptionAdvice;
    
    private static final String BLOCK_EXCEPTION_MESSAGE_PREFIX = "SentinelBlockException:";
    
    @Override
    public int getOrder()
    {
        return ORDER;
    }

    /** {@linkplain BlockException} 异常处理器 */
    @ExceptionHandler({BlockException.class})
    public Response<?> handleBlockException(HttpServletRequest request, HttpServletResponse response, BlockException e) throws BlockException
    {
        ServiceException se = wrapServiceException(FORBID_EXCEPTION, e);;
        logServiceException(log, se.getMessage(), se);
        
        return new Response<>(se);            
    }

    /** {@linkplain UndeclaredThrowableException} 异常处理器 */
    @ExceptionHandler({UndeclaredThrowableException.class})
    public Response<?> handleUndeclaredThrowableException(HttpServletRequest request, HttpServletResponse response, UndeclaredThrowableException e) throws UndeclaredThrowableException, Exception
    {
        Throwable t = e.getCause();
        
        if(t instanceof BlockException)
            return handleBlockException(request, response, (BlockException)t);

        return globalExceptionAdvice.handleException(request, response, e);
    }
    
    /** {@linkplain RuntimeException} 异常处理器 */
    @ExceptionHandler({RuntimeException.class})
    public Response<?> handleRuntimeException(HttpServletRequest request, HttpServletResponse response, RuntimeException e) throws Exception
    {
        ServiceException se = null;
        String message = e.getMessage();
        
        if(GeneralHelper.isStrNotEmpty(message))
        {
            if(message.startsWith(BLOCK_EXCEPTION_MESSAGE_PREFIX))
                se = wrapServiceException(FORBID_EXCEPTION, e);
        }
        
        if(se == null)
            return globalExceptionAdvice.handleException(request, response, e);

        logServiceException(log, se.getMessage(), se);
        
        return new Response<>(se);
    }

}
