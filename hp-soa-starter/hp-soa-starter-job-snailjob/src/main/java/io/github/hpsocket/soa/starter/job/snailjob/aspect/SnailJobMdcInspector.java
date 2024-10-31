
package io.github.hpsocket.soa.starter.job.snailjob.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;

import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.starter.job.snailjob.executor.JobInvoker;

/** <b>SnailJob 拦截器</b> */
@Aspect
@Order(SnailJobMdcInspector.ORDER)
public class SnailJobMdcInspector
{
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = """
                                                  (execution (public com.aizuda.snailjob.client.model.ExecuteResult *.*(com.aizuda.snailjob.client.job.core.dto.JobArgs)) && @annotation(com.aizuda.snailjob.client.job.core.annotation.JobExecutor)) ||
                                                  (execution (public com.aizuda.snailjob.client.model.ExecuteResult *.*(com.aizuda.snailjob.client.job.core.dto.JobArgs)) && @within(com.aizuda.snailjob.client.job.core.annotation.JobExecutor))
                                                  """;
    
    private static final AspectHelper.AnnotationHolder<JobExecutor> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};
    
    @Autowired
    private JobInvoker jobInvoker;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}

    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        JobExecutor job = ANNOTATION_HOLDER.findAnnotationByMethodOrClass(joinPoint);
        Assert.notNull(job, "@JobExecutor annotation not found");
        
        String jobName  = job.name();
        String method   = ((MethodSignature)joinPoint.getSignature()).getMethod().getName();
        JobArgs jobArgs = (JobArgs)joinPoint.getArgs()[0];
        
        return jobInvoker.invoke(() ->
        {
            try
            {
                return (ExecuteResult)joinPoint.proceed();
            }
            catch(Throwable e)
            {
                throw new RuntimeException(e.getMessage(), e);
            }
        }, jobArgs, jobName, method);
    }
    
}
