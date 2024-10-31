package io.github.hpsocket.soa.starter.job.snailjob.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/** <b>SnailJob Tracing 拦截器</b><br>
 * 用于注入 traceId
 */
@Aspect
@Order(Integer.MIN_VALUE)
public class SnailJobTracingInspector
{
    private static final String POINTCUT_PATTERN = """
                                                   (execution (public com.aizuda.snailjob.client.model.ExecuteResult io.github.hpsocket.soa.starter.job.snailjob.executor.JobInvoker.invoke(..)))
                                                   """;
    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Trace
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        return joinPoint.proceed();
    }
}
