package io.github.hpsocket.soa.framework.web.aspect;

import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.SiteLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.FORBID_EXCEPTION;

/** <b>内网 HTTP 请求拦截器</b><p>
 * 处理 {@linkplain SiteLocal} 注解
 */
@Aspect
@Order(Integer.MIN_VALUE)
public class SiteLocalInspector
{
    private static final String POINTCUT_PATTERN = AccessVerificationInspector.POINTCUT_PATTERN
                                                 + " && (@annotation(io.github.hpsocket.soa.framework.web.annotation.SiteLocal) || @within(io.github.hpsocket.soa.framework.web.annotation.SiteLocal))";

    @Pointcut(POINTCUT_PATTERN)
    protected void beforeMethod() {}
    
    @Before(value = "beforeMethod()")
    public void verifyRequestIP(JoinPoint point)
    {
        String ip = RequestContext.getClientAddr();
        
        if(!SystemUtil.isLocalNetworkAddress(ip))
            throw FORBID_EXCEPTION;
    }

}
