package org.hpsocket.soa.framework.web.server.spring.advice;

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
import org.hpsocket.soa.framework.web.server.RequestContext;
import org.hpsocket.soa.framework.web.server.model.RequestAttribute;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

import com.lcb.soa.misc.common.thread.MdcTracingThreadPoolExecutor;

import static com.lcb.soa.misc.server.ServerHelper.*;

@Slf4j
@Aspect
@Order(-2)
@Component
@RestControllerAdvice
public class ControllerRequestAdvice implements RequestBodyAdvice
{
	public static final String POINTCUT_PATTERN = "execution (public com.lcb.soa.misc.server.spring.model.Response com.lcb.hapi..controller..*.*(..))";
	
	public static final MdcTracingThreadPoolExecutor LOG_EXECUTOR = new MdcTracingThreadPoolExecutor(	true,
																										5,
																										30,
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
		
		final RequestAttribute requestAttribute = RequestContext.getRequestAttribute();
		
		try
		{
			LOG_EXECUTOR.execute(new Runnable()
			{	
				@Override
				public void run()
				{
					log.info("[ REQUEST: {} ] -> {}", requestAttribute.getRequestPath(), JSONObject.toJSONString(requestAttribute, JSON_SERIAL_FEATURES_NO_NULL_VAL));
				}
			});
		}
		catch(Exception e)
		{
			log.error("async write request log fail", e);
		}

	}

}
