package org.hpsocket.soa.framework.web.advice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hpsocket.soa.framework.core.mdc.MdcRunnable;
import org.hpsocket.soa.framework.web.support.RequestContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;

import static org.hpsocket.soa.framework.web.support.WebServerHelper.*;

@Slf4j
@Aspect
@Order(-10)
@RestControllerAdvice
public class ControllerRequestAdvice implements RequestBodyAdvice
{
	public static final String POINTCUT_PATTERN = "execution (public org.hpsocket.soa.framework.web.model.Response *.*(..)) && @within(org.springframework.web.bind.annotation.RestController)";
	
	public static final ThreadPoolExecutor LOG_EXECUTOR = new ThreadPoolExecutor(	5,
																					20,
																					60,
																					TimeUnit.SECONDS,
																					new LinkedBlockingDeque<>(5000),
																					new ThreadPoolExecutor.CallerRunsPolicy());																		 

	@PreDestroy
	public void preDestroy()
	{
		LOG_EXECUTOR.shutdown();
	}
	
	@Pointcut(POINTCUT_PATTERN)
	private void beforeMethod() {}
	
	@Before("beforeMethod()")
	public void before(JoinPoint joinPoint)
	{
		logRequest();
	}

	@InitBinder
	public void initBinder(WebDataBinder binder)
	{

	}
	
	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
	{
		return true;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
	{
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException
	{
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
	{
		RequestContext.setBody(body);
		
		return body;
	}

	private void logRequest()
	{
		if(!log.isInfoEnabled())
			return;
		
		try
		{
			LOG_EXECUTOR.execute(new MdcRunnable()
			{	
				@Override
				public void doRun()
				{
					log.info("[ REQUEST: {} ] -> {}", RequestContext.getRequestPath(), JSONObject.toJSONString(RequestContext.getRequestAttribute(), JSON_SERIAL_FEATURES_NO_NULL_VAL));
				}
			});
		}
		catch(Exception e)
		{
			log.error("async write request log fail", e);
		}
	}

}
