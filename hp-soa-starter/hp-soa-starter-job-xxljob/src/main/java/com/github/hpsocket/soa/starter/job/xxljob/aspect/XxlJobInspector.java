
package com.github.hpsocket.soa.starter.job.xxljob.aspect;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.web.service.AsyncService;
import com.github.hpsocket.soa.framework.web.support.AspectHelper;
import com.github.hpsocket.soa.framework.web.support.WebServerHelper;
import com.github.hpsocket.soa.starter.job.xxljob.exception.XxlJobExceptionHandler;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Order(0)
public class XxlJobInspector
{
	private static final int SKIP_RESULT_CODE	 = 202;
	private static final String POINTCUT_PATTERN = "execution (public void *.*()) && "
												 + "@annotation(com.xxl.job.core.handler.annotation.XxlJob)";
	
	@Autowired(required = false)
	AsyncService asyncService;
	@Autowired(required = false)
	private XxlJobExceptionHandler exceptionHandler;

	@Pointcut(POINTCUT_PATTERN)
	protected void aroundMethod()
	{
	}

	@Around(value = "aroundMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
	{
		MdcAttr mdcAttr = WebServerHelper.createMdcAddr(true);		

		XxlJob job		= AspectHelper.getMethodAnnotation(joinPoint, XxlJob.class);
		String jobName	= job.value();
		long jobId		= XxlJobHelper.getJobId();
		String param	= XxlJobHelper.getJobParam();
			
		try
		{
			mdcAttr.putMdc();
			
			if(WebServerHelper.isAppReadOnly())
			{
				String msg = String.format("current application is read only, skip xxl-job '%s'", jobName);
				
				log.debug(msg);
				XxlJobHelper.handleResult(SKIP_RESULT_CODE, msg);
				
				return null;
			}
			
			StopWatch sw = new StopWatch(jobName);
			
			try
			{
				log.debug("start xxl-job -> {} (id: {}, param: '{}')", jobName, jobId, param);
				
				sw.start();
				
				return joinPoint.proceed();
			}
			catch(Exception e)
			{
				Exception cause = null;
				
				if(e instanceof InvocationTargetException)
					cause = (Exception)e.getCause();
				if(cause == null)
					cause = e;
				
				log.error("execute xxl-job -> {} exception : {}", jobName, cause.getMessage(), cause);
				
				throw cause;
			}
			finally
			{
				sw.stop();
				
				log.debug("end xxl-job -> {} (id: {}, costTime: {})", jobName, jobId, sw.getLastTaskTimeMillis());				
			}
		}
		catch(Exception e)
		{
			invokeExceptionHandler(jobName, jobId, param, e);
			
			throw e;
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}

	private void invokeExceptionHandler(String jobName, long jobId, String param, Exception e)
	{
		if(exceptionHandler == null)
			return;
		
		if(asyncService == null)
			exceptionHandler.handleException(jobName, jobId, param, e);
		else
		{
			asyncService.execute(() -> exceptionHandler.handleException(jobName, jobId, param, e));
		}
	}
}
