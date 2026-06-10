package io.github.hpsocket.soa.starter.data.redis.consumer.aspect;

import com.alibaba.fastjson2.JSON;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.data.redis.exception.RedisListenerExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

/** <b>Redis Listener MDC 拦截器</b><br>
 * 用于注入 MDC 调用链跟踪信息
 */
@Slf4j
@Aspect
@Order(RedisListenerMdcInspector.ORDER)
public class RedisListenerMdcInspector
{
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = "target(org.redisson.api.listener.MessageListener) && execution (public void *.onMessage(java.lang.CharSequence, ..))";

    @Autowired(required = false)
    private AsyncService asyncService;
    @Autowired(required = false)
    private RedisListenerExceptionHandler exceptionHandler;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();
        mdcAttr.putMdc();

        String channel = joinPoint.getArgs()[0].toString();
        Object message = joinPoint.getArgs()[1];
        StopWatch sw   = null;
        
        try
        {
            if(log.isDebugEnabled())
            {
                log.debug("start process message -> (channel: {}) {}", channel, JSON.toJSONString(message));

                sw = new StopWatch(channel);
                sw.start();
            }
            
            return joinPoint.proceed();
        }
        catch(Exception e)
        {
            invokeExceptionHandler(channel, message, e);
            throw e;
        }
        finally
        {
            if(log.isDebugEnabled())
            {
                sw.stop();

                log.debug("end process message -> (channel: {}, costTime: {})", channel, sw.getTotalTimeMillis());
            }
            
            mdcAttr.removeMdc();
        }
    }

    private void invokeExceptionHandler(CharSequence channel, Object msg, Exception e)
    {
        try
        {
            if(exceptionHandler == null)
                return;

            long timestamp = System.currentTimeMillis();

            if(asyncService == null)
                exceptionHandler.handleException(channel, msg, timestamp, e);
            else
                asyncService.execute(() -> exceptionHandler.handleException(channel, msg, timestamp, e));
        }
        catch(Exception ex)
        {
            log.error("execute Invoke-Exception-Handler exception -> (channel: {}) : {}", channel, ex.getMessage(), ex);
        }
    }
}
