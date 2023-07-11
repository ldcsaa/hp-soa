package org.hpsocket.soa.framework.web.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hpsocket.soa.framework.core.util.SystemUtil;
import org.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import org.hpsocket.soa.framework.web.advice.RequestContext;

import static org.hpsocket.soa.framework.core.exception.ServiceException.*;

@Aspect
@Order(Integer.MIN_VALUE)
public class SiteLocalInspector
{
	private static final String POINTCUT_PATTERN = ControllerRequestAdvice.POINTCUT_PATTERN
					+ "&& (@annotation(org.hpsocket.soa.framework.web.annotation.SiteLocal) || @within(org.hpsocket.soa.framework.web.annotation.SiteLocal))";

	@Pointcut(POINTCUT_PATTERN)
	protected void beforeMethod() {}
	
	@Before(value = "beforeMethod()")
	public void verifyRequestIP(JoinPoint point)
	{
		String ip = RequestContext.getRemoteIp();
		
		if(!SystemUtil.isLocalNetwork(ip))
			throwServiceException("拒绝访问", FORBID_ERROR);
	}

}
