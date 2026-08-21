package io.github.hpsocket.soa.framework.web.advice;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.exception.UnseriousException;
import io.github.hpsocket.soa.framework.web.event.SeriousExceptionEvent;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.model.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

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
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Response<?> handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e)
    {
        BindingResult rs = e.getBindingResult();
        Map<String, List<String>> validationErrors = new LinkedHashMap<>();

        for(ObjectError obj : rs.getAllErrors())
        {
            if(obj instanceof FieldError field)
                parseError(field.getField(), field.getDefaultMessage(), validationErrors);
            else
                parseError("param-" + obj.getObjectName(), obj.getDefaultMessage(), validationErrors);
        }

        return makeValidationErrorResponse(e, validationErrors);
    }


    /** {@linkplain HandlerMethodValidationException} 异常处理器 */
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Response<?> handleHandlerMethodValidationException(HttpServletRequest request, HttpServletResponse response, HandlerMethodValidationException e)
    {
        List<ParameterValidationResult> rs = e.getParameterValidationResults();
        Map<String, List<String>> validationErrors = new LinkedHashMap<>();

        for(int i = 0; i < rs.size(); i++)
        {
            ParameterValidationResult obj = rs.get(i);
            List<MessageSourceResolvable> resolvableErrors = obj.getResolvableErrors();

            for(MessageSourceResolvable resolvable : resolvableErrors)
            {
                if(resolvable instanceof FieldError field)
                    parseError(field.getField(), resolvable.getDefaultMessage(), validationErrors);
                else
                    parseError("param-" + i, resolvable.getDefaultMessage(), validationErrors);
            }
        }

        return makeValidationErrorResponse(e, validationErrors);
    }

    private static void parseError(String fieldName, String errorMessage, Map<String, List<String>> validationErrors)
    {
        String name = fieldName;
        List<String> errs = validationErrors.get(name);

        if(errs == null)
        {
            errs = new LinkedList<>();
            validationErrors.put(name, errs);
        }

        errs.add(errorMessage);
    }

    private static Response<Object> makeValidationErrorResponse(Exception e, Map<String, List<String>> validationErrors)
    {
        ServiceException se = wrapServiceException(PARAM_VERIFY_EXCEPTION, e);
        logServiceException(log, se.getMessage(), se, false);

        return new Response<>(se, validationErrors);
    }

    /** {@linkplain Exception} 异常处理器 */
    @ExceptionHandler({Exception.class})
    public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
    {
        ServiceException se   = null;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if(e instanceof IllegalArgumentException)
        {
            httpStatus = HttpStatus.PRECONDITION_FAILED;
            se = wrapServiceException(PARAM_VALIDATION_EXCEPTION, e);
        }
        else if(e instanceof HttpRequestMethodNotSupportedException)
        {
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
            se = wrapServiceException(NOT_IMPLEMENTED_EXCEPTION, e);
        }
        else if(e instanceof HttpMediaTypeException)
        {
            httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            se = wrapServiceException(NOT_SUPPORTED_EXCEPTION, e);
        }
        else if(e instanceof HttpMessageConversionException)
        {
            httpStatus = HttpStatus.BAD_REQUEST;
            se = wrapServiceException(BAD_REQUEST_EXCEPTION, e);
        }
        else if(e instanceof NoHandlerFoundException || e instanceof NoResourceFoundException)
        {
            httpStatus = HttpStatus.NOT_FOUND;
            se = wrapServiceException(NOT_EXIST_EXCEPTION, e);
        }
        else if(e instanceof AuthenticationException)
        {
            httpStatus = HttpStatus.UNAUTHORIZED;
            se = wrapServiceException(AUTHEN_EXCEPTION, e);
        }
        else if(e instanceof AccessDeniedException)
        {
            httpStatus = HttpStatus.FORBIDDEN;
            se = wrapServiceException(AUTHOR_EXCEPTION, e);

        }
        else if(e instanceof ServiceException)
        {
            se = (ServiceException)e;
            httpStatus = parseHttpStatusCode(se);
        }

        if(se == null)
        {
            se = wrapServiceException(GENERAL_EXCEPTION, e);
        }

        boolean isSerious = isSeriousException(se);
        logServiceException(log, se.getMessage(), se, isSerious);

        if(isSerious)
        {
            SpringContextHolder.publishEvent(new SeriousExceptionEvent(e));
        }

        response.setStatus(httpStatus.value());
        return new Response<>(se);
    }

    private HttpStatus parseHttpStatusCode(ServiceException se)
    {
        HttpStatus httpStatus;
        switch(se.getStatusCode())
        {
            case OK -> httpStatus = HttpStatus.OK;
            case GENERAL_ERROR -> httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            case ACCEPTED -> httpStatus = HttpStatus.ACCEPTED;
            case NO_CONTENT -> httpStatus = HttpStatus.NO_CONTENT;
            case PARTIAL_OK -> httpStatus = HttpStatus.PARTIAL_CONTENT;
            case BAD_REQUEST,
                 BAD_REQUEST_PARAM -> httpStatus = HttpStatus.BAD_REQUEST;
            case NOT_EXIST -> httpStatus = HttpStatus.NOT_FOUND;
            case PARAM_VERIFY_ERROR,
                 PARAM_VALIDATION_ERROR -> httpStatus = HttpStatus.PRECONDITION_FAILED;
            case REPEATED_REQ_ERROR -> httpStatus = HttpStatus.CONFLICT;
            case NOT_IMPLEMENTED -> httpStatus = HttpStatus.NOT_IMPLEMENTED;
            case FREQUENCY_LIMIT_ERROR,
                 TRAFFIC_LIMIT_ERROR -> httpStatus = HttpStatus.TOO_MANY_REQUESTS;
            case FORBID_UPDATE_ERROR,
                 FORBID_ERROR,
                 AUTHOR_ERROR -> httpStatus = HttpStatus.FORBIDDEN;
            case NOT_SUPPORTED -> httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case TIMEOUT_ERROR -> httpStatus = HttpStatus.GATEWAY_TIMEOUT;
            case READ_ONLY_ERROR -> httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
            case APPCODE_CHECK_ERROR,
                 APPCODE_NOT_EXIST_ERROR,
                 APPCODE_EXIST_ERROR -> httpStatus = HttpStatus.NOT_ACCEPTABLE;
            case AUTHEN_ERROR,
                 SIGN_VERIFY_ERROR,
                 LOGIN_INVALID,
                 NOT_LOGGED_IN -> httpStatus = HttpStatus.UNAUTHORIZED;
            case NETWORK_ERROR,
                 OUTER_API_CALL_FAIL,
                 INNER_API_CALL_FAIL -> httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            default -> httpStatus = HttpStatus.EXPECTATION_FAILED;
        }

        return httpStatus;
    }

    private boolean isSeriousException(ServiceException se)
    {
        if(se instanceof UnseriousException)
            return false;

        return switch(se.getStatusCode())
        {
            case OK, ACCEPTED, NO_CONTENT, PARTIAL_OK, NOT_EXIST,
                 REPEATED_REQ_ERROR, FREQUENCY_LIMIT_ERROR, READ_ONLY_ERROR,
                 FORBID_ERROR, TRAFFIC_LIMIT_ERROR, LOGIN_INVALID,
                 NOT_LOGGED_IN, PARAM_VERIFY_ERROR, PARAM_VALIDATION_ERROR,
                 AUTHEN_ERROR, AUTHOR_ERROR, SIGN_VERIFY_ERROR, APPCODE_CHECK_ERROR,
                 APPCODE_NOT_EXIST_ERROR, APPCODE_EXIST_ERROR -> false;
            default -> true;
        };
    }

}
