package com.github.hpsocket.soa.starter.sentinel.advice;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.hpsocket.soa.framework.core.exception.ServiceException;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import com.github.hpsocket.soa.framework.web.model.Response;

import static com.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class SentinelExceptionAdvice implements Ordered
{
	@Autowired
	ControllerGlobalExceptionAdvice globalExceptionAdvice;
	
	private static final String BLOCK_EXCEPTION_MESSAGE_PREFIX = "SentinelBlockException:";
	
	@Override
	public int getOrder()
	{
		return -100;
	}

	@ExceptionHandler({BlockException.class})
	public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, BlockException e)
	{
		ServiceException se = wrapServiceException(FORBID_EXCEPTION, e);;
		logServiceException(log, se.getMessage(), se);
		
		return new Response<>(se);			
	}

	@ExceptionHandler({UndeclaredThrowableException.class})
	public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, UndeclaredThrowableException e)
	{
		Throwable t = e.getCause();
		
		if(t instanceof BlockException)
			return handleException(request, response, (BlockException)t);

		return globalExceptionAdvice.handleException(request, response, e);
	}
	
	@ExceptionHandler({RuntimeException.class})
	public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, RuntimeException e)
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
