package com.github.hpsocket.soa.starter.task.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.github.hpsocket.soa.framework.web.support.WebServerHelper;

@Aspect
@Order(Integer.MIN_VALUE)
public class ScheduledTraceIdInspector
{
	private static final String POINTCUT_PATTERN = "execution (public void *.*()) && "
												 + "@annotation(org.springframework.scheduling.annotation.Scheduled)";

	@Pointcut(POINTCUT_PATTERN)
	protected void aroundMethod() {}
	
	@Around(value = "aroundMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
	{
		WebServerHelper.putMdcTraceId();
		
		try
		{
			return joinPoint.proceed();
		}
		finally
		{
			WebServerHelper.removeMdcTraceId();
		}
	}

}

