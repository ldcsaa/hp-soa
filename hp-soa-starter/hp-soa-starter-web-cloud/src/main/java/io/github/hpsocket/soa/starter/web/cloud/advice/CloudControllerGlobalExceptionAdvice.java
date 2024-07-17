package io.github.hpsocket.soa.starter.web.cloud.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>Spring Cloud HTTP 请求全局异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class CloudControllerGlobalExceptionAdvice extends ControllerGlobalExceptionAdvice
{
    /** {@linkplain MethodArgumentNotValidException} 异常处理器 */
    @Override
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Response<?> handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e)
    {
        if(WebServerHelper.isEntry())
            return super.handleMethodArgumentNotValidException(request, response, e);
        else
        {
            log.error(e.getMessage());
            return TracingHelper.createExceptionResponse(e, HttpStatus.BAD_REQUEST);
        }
    }

    /** {@linkplain Exception} 异常处理器 */
    @Override
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({Exception.class})
    public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
    {
        if(WebServerHelper.isEntry())
            return super.handleException(request, response, e);
        else
        {
            log.error(e.getMessage(), e);
            return TracingHelper.createExceptionResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
