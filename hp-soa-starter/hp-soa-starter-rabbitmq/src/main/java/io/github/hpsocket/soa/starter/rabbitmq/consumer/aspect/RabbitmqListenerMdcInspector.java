package io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.rabbitmq.stream.Properties;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

import java.util.Map;

/** <b>Rabbitmq Listener MDC 拦截器</b><br>
 * 用于注入 MDC 调用链跟踪信息
 */
@Slf4j
@Aspect
@Order(RabbitmqListenerMdcInspector.ORDER)
public class RabbitmqListenerMdcInspector
{
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = "execution (public void *.*(..)) && "
                                                + "("
                                                + "     @annotation(org.springframework.amqp.rabbit.annotation.RabbitListener) || "
                                                + "     ("
                                                + "         @target(org.springframework.amqp.rabbit.annotation.RabbitListener) &&"
                                                + "         @annotation(org.springframework.amqp.rabbit.annotation.RabbitHandler)"
                                                + "     )"
                                                + ")";

    private static final AspectHelper.AnnotationHolder<RabbitListener> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();        

        RabbitListener listener = ANNOTATION_HOLDER.findAnnotationByMethodOrClass(joinPoint);
        Assert.notNull(listener, "@RabbitListener annotation not found");
        
        String listenerId        = listener.id();
        String messageId         = null;
        String internalMessageId = null;
        String sourceRequestId   = null;
        String domainName        = null;
        String eventName         = null;
        String correlationId     = null;
        
        Object obj = AspectHelper.findFirstArgByTypes(joinPoint, Message.class
                                                    , org.springframework.messaging.Message.class
                                                    , com.rabbitmq.stream.Message.class);
        
        if(obj != null)
        {
            if(obj instanceof Message msg)
            {
                MessageProperties properties = msg.getMessageProperties();
                
                messageId         = properties.getHeader(HEADER_MSG_ID);
                sourceRequestId   = properties.getHeader(HEADER_SOURCE_REQUEST_ID);
                domainName        = properties.getHeader(HEADER_DOMAIN_NAME);
                eventName         = properties.getHeader(HEADER_EVENT_NAME);
                correlationId     = properties.getHeader(HEADER_CORRELATION_ID);
                internalMessageId = properties.getMessageId();
                
                if(GeneralHelper.isStrEmpty(correlationId))
                    correlationId = properties.getCorrelationId();
            }
            else if(obj instanceof org.springframework.messaging.Message msg)
            {
                MessageHeaders headers = msg.getHeaders();
                
                messageId         = (String)headers.get(HEADER_MSG_ID);
                sourceRequestId   = (String)headers.get(HEADER_SOURCE_REQUEST_ID);
                domainName        = (String)headers.get(HEADER_DOMAIN_NAME);
                eventName         = (String)headers.get(HEADER_EVENT_NAME);
                correlationId     = (String)headers.get(HEADER_CORRELATION_ID);
                internalMessageId = (String)headers.get(HEADER_AMQP_MESSAGE_ID);
                
                if(GeneralHelper.isStrEmpty(correlationId))
                    correlationId = (String)headers.get(HEADER_CORRELATION_ID);
            }
            else if(obj instanceof com.rabbitmq.stream.Message msg)
            {
                Properties props  = msg.getProperties();
                internalMessageId = props.getMessageIdAsString();
                
                Map<String, Object> appProps = msg.getApplicationProperties();

                
                if(appProps != null)
                {
                    messageId       = (String)appProps.get(HEADER_MSG_ID);
                    sourceRequestId = (String)appProps.get(HEADER_SOURCE_REQUEST_ID);
                    domainName      = (String)appProps.get(HEADER_DOMAIN_NAME);
                    eventName       = (String)appProps.get(HEADER_EVENT_NAME);
                    correlationId   = (String)appProps.get(HEADER_CORRELATION_ID);
                                      
                    if(GeneralHelper.isStrEmpty(correlationId))
                        correlationId = props.getCorrelationIdAsString();
                }
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
                log.trace("rabbit listener start consume message -> (listenerId: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {})"
                    , listenerId, correlationId, messageId, sourceRequestId, domainName, eventName);
            
                sw = new StopWatch(listenerId);
                sw.start();
            }
            
            return joinPoint.proceed();
        }
        finally
        {
            if(log.isTraceEnabled())
            {
                sw.stop();

                log.trace("rabbit listener end consume message -> (listenerId: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {}, costTime: {})"
                    , listenerId, correlationId, messageId, sourceRequestId, domainName, eventName, sw.getLastTaskTimeMillis());                
            }
            
            mdcAttr.removeMdc();
        }
    }
}
