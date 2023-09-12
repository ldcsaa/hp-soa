package io.github.hpsocket.soa.framework.web.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.SiteLocal;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

/** <b>内网 HTTP 请求拦截器</b><br>
 * 处理 {@linkplain SiteLocal} 注解
 */
@Aspect
@Order(Integer.MIN_VALUE)
public class SiteLocalInspector
{
	private static final String POINTCUT_PATTERN = AccessVerificationInspector.POINTCUT_PATTERN
					+ "&& (@annotation(io.github.hpsocket.soa.framework.web.annotation.SiteLocal) || @within(io.github.hpsocket.soa.framework.web.annotation.SiteLocal))";

	@Pointcut(POINTCUT_PATTERN)
	protected void beforeMethod() {}
	
	@Before(value = "beforeMethod()")
	public void verifyRequestIP(JoinPoint point)
	{
		String ip = RequestContext.getClientAddr();
		
		if(!SystemUtil.isLocalNetwork(ip))
			throw FORBID_EXCEPTION;
	}

}
