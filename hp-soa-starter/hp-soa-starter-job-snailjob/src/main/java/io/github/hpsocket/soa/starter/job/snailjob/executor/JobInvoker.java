package io.github.hpsocket.soa.starter.job.snailjob.executor;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.job.snailjob.exception.SnailJobExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/** <b>SnailJob 执行者</b><br>
 * （注：当应用程序为只读时，不执行任何 Job 业务逻辑）
 */
@Slf4j
public class JobInvoker
{
    @Autowired(required = false)
    private AsyncService asyncService;
    @Autowired(required = false)
    private SnailJobExceptionHandler exceptionHandler;
    
    public ExecuteResult invoke(Supplier<ExecuteResult> supplier, JobArgs jobArgs, String jobName, String method)
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();
        
        Long jobId      = jobArgs.getJobId();
        String param    = GeneralHelper.safeString(jobArgs.getJobParams());
        
        try
        {
            mdcAttr.putMdc();
            
            if(WebServerHelper.isAppReadOnly())
            {
                String msg = String.format("current application is read only, skip snail-job '%s'", jobName);

                if(log.isDebugEnabled())
                    log.debug(msg);
                
                return ExecuteResult.failure(null, msg);
            }
            
            StopWatch sw = null;
            
            try
            {
                if(log.isDebugEnabled())
                {
                    log.debug(">>>>>>>>>> start snail-job -> (name: {}, id: {}, method: {}, param: '{}')", jobName, jobId, method, param);
                
                    sw = new StopWatch(jobName);
                    sw.start();
                }
                
                return supplier.get();
            }
            catch(Exception e)
            {
                Exception cause = null;
                
                if(e instanceof InvocationTargetException)
                    cause = (Exception)e.getCause();
                if(cause == null)
                    cause = e;
                
                log.error("execute snail-job exception -> ({}) : {}", jobName, cause.getMessage(), cause);
                
                throw cause;
            }
            finally
            {
                if(log.isDebugEnabled())
                {
                    sw.stop();
                    
                    log.debug("<<<<<<<<<< end snail-job -> (name: {}, id: {}, method: {}, costTime: {})", jobName, jobId, method, sw.getTotalTimeMillis());
                }
            }
        }
        catch(Exception e)
        {
            invokeExceptionHandler(jobName, jobId, method, param, e);
            
            throw new RuntimeException(e);
        }
        finally
        {
            mdcAttr.removeMdc();
        }
    }

    private void invokeExceptionHandler(String jobName, Long jobId, String method, String param, Exception e)
    {
        try
        {
            if(exceptionHandler == null)
                return;
            
            long timestamp = System.currentTimeMillis();
            
            if(asyncService == null)
                exceptionHandler.handleException(jobName, jobId, method, param, timestamp, e);
            else
                asyncService.execute(() -> exceptionHandler.handleException(jobName, jobId, method, param, timestamp, e));
        }
        catch(Exception ex)
        {
            log.error("execute snail-job Invoke-Exception-Handler exception -> ({}) : {}", jobName, ex.getMessage(), ex);
        }
    }
}
