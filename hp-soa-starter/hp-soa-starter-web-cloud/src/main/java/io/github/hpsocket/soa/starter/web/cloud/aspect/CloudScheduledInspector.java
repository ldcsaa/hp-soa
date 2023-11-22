package io.github.hpsocket.soa.starter.web.cloud.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.github.hpsocket.soa.starter.task.aspect.ScheduledTraceIdInspector;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

/** <b>Spring Cloud {@linkplain org.springframework.scheduling.annotation.Scheduled Scheduled} 拦截器</b><br>
 * 为 {@linkplain org.springframework.scheduling.annotation.Scheduled Scheduled} 注解的方法注入调用链跟踪信息
 */
@Aspect
@Order(CloudScheduledInspector.ORDER)
public class CloudScheduledInspector
{
    public static final int ORDER = ScheduledTraceIdInspector.ORDER + 10;
    
    public static final String POINTCUT_PATTERN = ScheduledTraceIdInspector.POINTCUT_PATTERN;
    
    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        TracingHelper.checkEntry();
        TracingHelper.fillBaggageByMdcAttr();
        
        return joinPoint.proceed();
    }

}
