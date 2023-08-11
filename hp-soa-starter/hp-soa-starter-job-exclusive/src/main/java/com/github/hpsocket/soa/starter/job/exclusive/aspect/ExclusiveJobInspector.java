
package com.github.hpsocket.soa.starter.job.exclusive.aspect;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import com.github.hpsocket.soa.framework.web.service.AsyncService;
import com.github.hpsocket.soa.framework.web.support.AspectHelper;
import com.github.hpsocket.soa.framework.web.support.WebServerHelper;
import com.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;
import com.github.hpsocket.soa.starter.job.exclusive.exception.ExclusiveJobExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Order(0)
public class ExclusiveJobInspector
{
	private static final String JOB_LOCK_KEY_PREFIX	= "hp.soa.job:lock:";
	private static final String POINTCUT_PATTERN 	= "execution (public void *.*()) && "
													+ "@annotation(com.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob)";
	
	private static final AtomicBoolean running = new AtomicBoolean(false);
	
	@Autowired(required = false)
	ExclusiveJobExceptionHandler exceptionHandler;
	
	@Autowired(required = false)
	AsyncService asyncService;
	@Autowired
	private RedissonClient redissonClient;

	@Pointcut(POINTCUT_PATTERN)
	protected void aroundMethod()
	{
	}

	@Around(value = "aroundMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
	{	
		MdcAttr mdcAttr = WebServerHelper.createMdcAddr(true);		

		ExclusiveJob job = AspectHelper.getMethodAnnotation(joinPoint, ExclusiveJob.class);
		String prefix = job.prefix();
		String jobName = job.jobName();
		
		if(GeneralHelper.isStrEmpty(prefix))
			prefix = AppConfigHolder.getAppName();
		
		String fullJobName = prefix + ':' + jobName;
		boolean needThrow  = true;

		try
		{
			if(!running.compareAndSet(false, true))
				return null;
			
			mdcAttr.putMdc();
			
			if(WebServerHelper.isAppReadOnly())
			{
				String msg = String.format("current application is read only, skip exclusive job '%s'", fullJobName);
				
				log.debug(msg);
				
				return null;
			}
			
			String lockKey	= JOB_LOCK_KEY_PREFIX + fullJobName;
			RLock lock		= redissonClient.getLock(lockKey);
			
			if(lock.tryLock(0, job.maxLockTime(), job.lockTimeUnit()))
			{
				StopWatch sw = new StopWatch(fullJobName);
				
				try
				{
					log.debug("start exclusive job -> {}", fullJobName);
					
					sw.start();

					return joinPoint.proceed();	
				}
				catch(Exception e)
				{
					log.error("execute exclusive job -> {} exception : {}", fullJobName, e.getMessage(), e);
					
					needThrow = false;
					throw e;
				}
				finally
				{
					sw.stop();
					
					try {lock.unlock();} catch(Exception e) {}
					log.debug("end exclusive job -> {} (costTime: {})", fullJobName, sw.getLastTaskTimeMillis());				
				}
			}
		}
		catch(Exception e)
		{
			invokeExceptionHandler(prefix, jobName, e);
			
			if(needThrow)
				throw e;
		}
		finally
		{
			running.compareAndSet(true, false);
			mdcAttr.removeMdc();
		}
		
		return null;
	}

	private void invokeExceptionHandler(String prefix, String jobName, Exception e)
	{
		if(exceptionHandler == null)
			return;
		
		if(asyncService == null)
			exceptionHandler.handleException(prefix, jobName, e);
		else
		{
			asyncService.execute(() -> exceptionHandler.handleException(prefix, jobName, e));
		}
	}
}
