package io.github.hpsocket.soa.starter.web.cloud.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.web.cloud.support.TraceHelper;

/** <b>Spring Cloud Rabbitmq Listener Mdc 拦截器</b><br>
 * 为 Rabbitmq Listener 方法注入调用链跟踪信息
 */
@Aspect
@Order(CloudRabbitmqListenerMdcInspector.ORDER)
public class CloudRabbitmqListenerMdcInspector
{
    public static final int ORDER = RabbitmqListenerMdcInspector.ORDER + 30;
    
    public static final String POINTCUT_PATTERN = RabbitmqListenerMdcInspector.POINTCUT_PATTERN;
    
    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        TraceHelper.fillBaggageByMdcAttr();
        return joinPoint.proceed();
    }

}
