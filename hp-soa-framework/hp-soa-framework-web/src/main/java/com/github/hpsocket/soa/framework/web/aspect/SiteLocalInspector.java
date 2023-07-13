package com.github.hpsocket.soa.framework.web.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import com.github.hpsocket.soa.framework.core.util.SystemUtil;
import com.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import com.github.hpsocket.soa.framework.web.advice.RequestContext;

import static com.github.hpsocket.soa.framework.core.exception.ServiceException.*;

@Aspect
@Order(Integer.MIN_VALUE)
public class SiteLocalInspector
{
	private static final String POINTCUT_PATTERN = ControllerRequestAdvice.POINTCUT_PATTERN
					+ "&& (@annotation(com.github.hpsocket.soa.framework.web.annotation.SiteLocal) || @within(com.github.hpsocket.soa.framework.web.annotation.SiteLocal))";

	@Pointcut(POINTCUT_PATTERN)
	protected void beforeMethod() {}
	
	@Before(value = "beforeMethod()")
	public void verifyRequestIP(JoinPoint point)
	{
		String ip = RequestContext.getRemoteIp();
		
		if(!SystemUtil.isLocalNetwork(ip))
			throw FORBID_EXCEPTION;
	}

}
