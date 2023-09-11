package io.github.hpsocket.soa.starter.task.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Scheduled;

import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import lombok.extern.slf4j.Slf4j;

/** <b>{@linkplain Scheduled} traceId 拦截器</b><br>
 * 为 {@linkplain Scheduled} 注解的方法注入 traceId 调用链跟踪信息
 */
@Slf4j
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
		
		if(WebServerHelper.isAppReadOnly())
		{
			String methodName = AspectHelper.getMethod(joinPoint).getName();
			String msg = String.format("current application is read only, skip scheduled task '%s'", methodName);
			
			log.debug(msg);
			
			return null;
		}
		
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

