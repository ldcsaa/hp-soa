package io.github.hpsocket.soa.starter.web.cloud.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.github.hpsocket.soa.starter.job.exclusive.aspect.ExclusiveJobInspector;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

/** <b>Spring Cloud Exclusive Job 拦截器</b><br>
 * 为 Exclusive Job 注解的方法注入调用链跟踪信息
 */
@Aspect
@Order(CloudExclusiveJobInspector.ORDER)
public class CloudExclusiveJobInspector
{
    public static final int ORDER = ExclusiveJobInspector.ORDER + 10;
    
    public static final String POINTCUT_PATTERN = ExclusiveJobInspector.POINTCUT_PATTERN;
    
    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        return TracingHelper.aspectInspect(joinPoint);
    }

}
