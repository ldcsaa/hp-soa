package io.github.hpsocket.soa.framework.web.advice;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.model.Response;

import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>HTTP 请求全局异常拦截器</b> */
@Slf4j
@RestControllerAdvice
public class ControllerGlobalExceptionAdvice implements Ordered
{
    public static final int ORDER = 0;
    
    @Override
    public int getOrder()
    {
        return ORDER;
    }

    /** {@linkplain MethodArgumentNotValidException} 异常处理器 */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Response<?> handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e) throws MethodArgumentNotValidException
    {
        BindingResult rs = e.getBindingResult();
        
        Map<String, List<String>> validationErrors = new LinkedHashMap<>();
        
        for(ObjectError obj : rs.getAllErrors())
        {
            if(obj instanceof FieldError field)
            {
                String name = field.getField();
                
                List<String> errs = validationErrors.get(name);
                
                if(errs == null)
                {
                    errs = new LinkedList<>();
                    validationErrors.put(name, errs);
                }
                
                errs.add(field.getDefaultMessage());
            }
        }
        
        if(validationErrors.isEmpty())
            validationErrors.put(rs.getObjectName(), Arrays.asList(rs.toString()));
        
        ServiceException se = wrapServiceException(PARAM_VERIFY_EXCEPTION, e);
        
        logServiceException(log, se.getMessage(), se);
        
        return new Response<>(se, validationErrors);
    }

    /** {@linkplain Exception} 异常处理器 */
    @ExceptionHandler({Exception.class})
    public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception
    {
        ServiceException se = null;
        
        if(e instanceof NoHandlerFoundException)
            se = wrapServiceException(NOT_EXIST_EXCEPTION, e);
        else if(e instanceof HttpRequestMethodNotSupportedException)
            se = wrapServiceException(NOT_IMPLEMENTED_EXCEPTION, e);
        else if(e instanceof HttpMediaTypeException)
            se = wrapServiceException(NOT_SUPPORTED_EXCEPTION, e);
        else if(e instanceof HttpMessageConversionException)
            se = wrapServiceException(BAD_REQUEST_EXCEPTION, e);
        
        if(se == null)
            se = wrapServiceException(GENERAL_EXCEPTION, e);

        logServiceException(log, se.getMessage(), se);
        
        return new Response<>(se);
    }

}
