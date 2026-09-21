package io.github.hpsocket.soa.starter.rocketmq.consumer.aspect;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.rocketmq.exception.RocketmqExceptionHandler;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StopWatch;

import java.util.Map;

/** <b>RocketMQ Listener MDC 拦截器</b><br>
 * 用于注入 MDC 调用链跟踪信息
 */
@Slf4j
@Aspect
@Order(RocketmqListenerMdcInspector.ORDER)
public class RocketmqListenerMdcInspector
{
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = "(execution (public org.apache.rocketmq.client.apis.consumer.ConsumeResult *.consume(org.apache.rocketmq.client.apis.message.MessageView+)) && @within(org.apache.rocketmq.client.annotation.RocketMQMessageListener)) ||"
                                                + "(execution (public void *.consume(org.apache.rocketmq.client.apis.message.MessageView+, io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate+)) && @within(io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleMessageListener)) ||"
                                                + "(execution (public org.apache.rocketmq.client.apis.producer.TransactionResolution *.check(org.apache.rocketmq.client.apis.message.MessageView+)) && @within(org.apache.rocketmq.client.annotation.RocketMQTransactionListener))"
                                                ;

    @Autowired(required = false)
    private AsyncService asyncService;
    @Autowired(required = false)
    private RocketmqExceptionHandler exceptionHandler;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();

        String correlationId     = null;
        String messageId         = null;
        String internalMessageId = null;
        String sourceRequestId   = null;
        String domainName        = null;
        String eventName         = null;
        String topic             = null;
        
        MessageView msg = AspectHelper.findFirstArgByType(joinPoint, MessageView.class);
        
        if(msg != null)
        {
            topic = msg.getTopic();
            internalMessageId = msg.getMessageId().toString();
            Map<String, String> properties = msg.getProperties();
            
            if(GeneralHelper.isNotNullOrEmpty(properties))
            {
                messageId       = properties.get(RocketmqConstant.HEADER_MSG_ID);
                sourceRequestId = properties.get(RocketmqConstant.HEADER_SOURCE_REQUEST_ID);
                domainName      = properties.get(RocketmqConstant.HEADER_DOMAIN_NAME);
                eventName       = properties.get(RocketmqConstant.HEADER_EVENT_NAME);
                correlationId   = properties.get(RocketmqConstant.HEADER_CORRELATION_ID);
           }
            
            if(GeneralHelper.isStrEmpty(messageId))
                messageId = internalMessageId;
            
            if(GeneralHelper.isStrNotEmpty(messageId))
                mdcAttr.setMessageId(messageId);
            if(GeneralHelper.isStrNotEmpty(internalMessageId))
                mdcAttr.setInternalMessageId(internalMessageId);
            if(GeneralHelper.isStrNotEmpty(sourceRequestId))
                mdcAttr.setSourceRequestId(sourceRequestId);
        }
        
        mdcAttr.putMdc();
        
        StopWatch sw = null;
        
        try
        {
            if(log.isDebugEnabled())
            {
                log.debug("start process message -> (topic: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {})"
                        , topic, correlationId, messageId, sourceRequestId, domainName, eventName);
                
                sw = new StopWatch();
                sw.start();
            }
            
            return joinPoint.proceed();
        }
        catch(Exception e)
        {
            invokeExceptionHandler(msg, messageId, e);
            throw e;
        }
        finally
        {
            
            if(log.isDebugEnabled())
            {
                sw.stop();

                log.debug("end process message -> (topic: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {}, costTime: {})"
                        , topic, correlationId, messageId, sourceRequestId, domainName, eventName, sw.getTotalTimeMillis());
            }

            mdcAttr.removeMdc();
        }
    }

    private void invokeExceptionHandler(MessageView msg, String messageId, Exception e)
    {
        try
        {
            if(exceptionHandler == null)
                return;

            long timestamp = System.currentTimeMillis();

            if(asyncService == null)
                exceptionHandler.handleException(msg, messageId, timestamp, e);
            else
                asyncService.execute(() -> exceptionHandler.handleException(msg, messageId, timestamp, e));
        }
        catch(Exception ex)
        {
            log.error("execute Invoke-Exception-Handler exception -> (messageId: {}) : {}", messageId, ex.getMessage(), ex);
        }
    }
}
