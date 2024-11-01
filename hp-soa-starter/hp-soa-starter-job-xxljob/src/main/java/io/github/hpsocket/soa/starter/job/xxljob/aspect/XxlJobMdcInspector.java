
package io.github.hpsocket.soa.starter.job.xxljob.aspect;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.job.xxljob.exception.XxlJobExceptionHandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.extern.slf4j.Slf4j;

/** <b>XxlJob 拦截器</b><br>
 * （注：当应用程序为只读时，不执行任何 Job 业务逻辑）
 */
@Slf4j
@Aspect
@Order(XxlJobMdcInspector.ORDER)
public class XxlJobMdcInspector
{
    public static final int SKIP_RESULT_CODE    = 202;
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = "execution (public void *.*()) && "
                                                + "@annotation(com.xxl.job.core.handler.annotation.XxlJob)";
    
    private static final AspectHelper.AnnotationHolder<XxlJob> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};
    
    @Autowired(required = false)
    private AsyncService asyncService;
    @Autowired(required = false)
    private XxlJobExceptionHandler exceptionHandler;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}

    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();        

        XxlJob job = ANNOTATION_HOLDER.findAnnotationByMethod(joinPoint);
        Assert.notNull(job, "@XxlJob annotation not found");
        
        String jobName  = job.value();
        long jobId      = XxlJobHelper.getJobId();
        String param    = XxlJobHelper.getJobParam();
            
        try
        {
            mdcAttr.putMdc();
            
            if(WebServerHelper.isAppReadOnly())
            {
                String msg = String.format("current application is read only, skip xxl-job '%s'", jobName);

                if(log.isDebugEnabled())
                    log.debug(msg);    
                
                XxlJobHelper.handleResult(SKIP_RESULT_CODE, msg);
                
                return null;
            }
            
            StopWatch sw = null;
            
            try
            {
                if(log.isDebugEnabled())
                {
                    log.debug(">>>>>>>>>> start xxl-job -> (name: {}, id: {}, param: '{}')", jobName, jobId, param);
                
                    sw = new StopWatch(jobName);
                    sw.start();
                }
                
                return joinPoint.proceed();
            }
            catch(Exception e)
            {
                Exception cause = null;
                
                if(e instanceof InvocationTargetException)
                    cause = (Exception)e.getCause();
                if(cause == null)
                    cause = e;
                
                log.error("execute xxl-job exception -> ({}) : {}", jobName, cause.getMessage(), cause);
                
                throw cause;
            }
            finally
            {
                if(log.isDebugEnabled())
                {
                    sw.stop();
                    
                    log.debug("<<<<<<<<<< end xxl-job -> (name: {}, id: {}, costTime: {})", jobName, jobId, sw.getTotalTimeMillis());
                }
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
        try
        {
            if(exceptionHandler == null)
                return;
            
            long timestamp = System.currentTimeMillis();
            
            if(asyncService == null)
                exceptionHandler.handleException(jobName, jobId, param, timestamp, e);
            else
                asyncService.execute(() -> exceptionHandler.handleException(jobName, jobId, param, timestamp, e));
        }
        catch(Exception ex)
        {
            log.error("execute xxl-job Invoke-Exception-Handler exception -> ({}) : {}", jobName, ex.getMessage(), ex);
        }
    }
}
