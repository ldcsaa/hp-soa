package com.github.hpsocket.soa.framework.web.advice;

import java.lang.reflect.Type;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.annotation.Aspect;
import com.github.hpsocket.soa.framework.core.mdc.MdcRunnable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;

import com.github.hpsocket.soa.framework.web.model.RequestAttribute;

import static com.github.hpsocket.soa.framework.web.support.WebServerHelper.*;

@Slf4j
@Aspect
@Order(-10)
@RestControllerAdvice
public class ControllerRequestAdvice extends RequestBodyAdviceAdapter
{
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
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
	{
		RequestContext.setBody(body);
		
		logRequest();
		
		return body;
	}

	private void logRequest()
	{
		if(!log.isInfoEnabled())
			return;
		
		try
		{
			final RequestAttribute requestAttribute = RequestContext.getRequestAttribute();
			
			LOG_EXECUTOR.execute(new MdcRunnable()
			{	
				@Override
				protected void doRun()
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
