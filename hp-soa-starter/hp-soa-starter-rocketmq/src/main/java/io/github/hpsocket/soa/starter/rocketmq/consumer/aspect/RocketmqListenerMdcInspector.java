package io.github.hpsocket.soa.starter.rocketmq.consumer.aspect;

import java.util.Map;

import org.apache.logging.log4j.core.config.Order;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.util.StopWatch;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.extern.slf4j.Slf4j;

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
            if(log.isTraceEnabled())
            {
                log.trace("start process message -> (topic: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {})"
                    , topic, correlationId, messageId, sourceRequestId, domainName, eventName);
                
                sw = new StopWatch();
                sw.start();
            }
            
            return joinPoint.proceed();
        }
        finally
        {
            
            if(log.isTraceEnabled())
            {
                sw.stop();

                log.trace("end process message -> (topic: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {}, costTime: {})"
                    , topic, correlationId, messageId, sourceRequestId, domainName, eventName, sw.lastTaskInfo().getTimeMillis());
            }

            mdcAttr.removeMdc();
        }
    }
}
