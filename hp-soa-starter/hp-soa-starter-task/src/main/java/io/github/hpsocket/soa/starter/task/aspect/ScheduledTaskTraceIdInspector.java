package io.github.hpsocket.soa.starter.task.aspect;

import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.task.exception.ScheduledTaskExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Scheduled;

/** <b>{@linkplain Scheduled} traceId 拦截器</b><br>
 * 为 {@linkplain Scheduled} 注解的方法注入 traceId 调用链跟踪信息
 */
@Slf4j
@Aspect
@Order(ScheduledTaskTraceIdInspector.ORDER)
public class ScheduledTaskTraceIdInspector
{
    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE;
    
    public static final String POINTCUT_PATTERN = "execution (public * *.*()) && "
                                                + "@annotation(org.springframework.scheduling.annotation.Scheduled)";

    @Autowired(required = false)
    private ScheduledTaskExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private AsyncService asyncService;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        WebServerHelper.putMdcTraceId();
        
        if(WebServerHelper.isAppReadOnly())
        {
            if(log.isDebugEnabled())
                log.debug("current application is read only, skip scheduled-task '{}'", AspectHelper.getMethod(joinPoint).getName());

            return null;
        }
        
        try
        {
            return joinPoint.proceed();
        }
        catch(Exception e)
        {
            String methodName = AspectHelper.getMethod(joinPoint).getName();
            invokeExceptionHandler(methodName, e);

            throw e;
        }
        finally
        {
            WebServerHelper.removeMdcTraceId();
        }
    }

    private void invokeExceptionHandler(String methodName, Exception e)
    {
        try
        {
            if(exceptionHandler == null)
                return;

            long timestamp = System.currentTimeMillis();

            if(asyncService == null)
                exceptionHandler.handleException(methodName, timestamp, e);
            else
                asyncService.execute(() -> exceptionHandler.handleException(methodName, timestamp, e));
        }
        catch(Exception ex)
        {
            log.error("execute scheduled-task Invoke-Exception-Handler exception -> ({}) : {}", methodName, ex.getMessage(), ex);
        }
    }
}

