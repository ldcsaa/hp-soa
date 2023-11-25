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
        
        String listenerId       = listener.id();
        String correlationId    = null;
        String messageId        = null;
        String sourceRequestId  = null;
        String domainName       = null;
        String eventName        = null;
        
        Object obj = AspectHelper.findFirstArgByTypes(joinPoint, Message.class
                                                    , org.springframework.messaging.Message.class
                                                    , com.rabbitmq.stream.Message.class);
        
        if(obj != null)
        {
            if(obj instanceof Message msg)
            {
                MessageProperties properties = msg.getMessageProperties();
                messageId       = properties.getMessageId();
                correlationId   = properties.getCorrelationId();
                sourceRequestId = properties.getHeader(HEADER_SOURCE_REQUEST_ID);
                domainName      = properties.getHeader(HEADER_DOMAIN_NAME);
                eventName       = properties.getHeader(HEADER_EVENT_NAME);                
            }
            else if(obj instanceof org.springframework.messaging.Message msg)
            {
                MessageHeaders headers = msg.getHeaders();
                messageId       = (String)headers.get(HEADER_MSG_ID);
                correlationId   = (String)headers.get(HEADER_CORRELA_DATA_ID);
                sourceRequestId = (String)headers.get(HEADER_SOURCE_REQUEST_ID);
                domainName      = (String)headers.get(HEADER_DOMAIN_NAME);
                eventName       = (String)headers.get(HEADER_EVENT_NAME);
            }
            else if(obj instanceof com.rabbitmq.stream.Message msg)
            {
                Properties props = msg.getProperties();
                Map<String, Object> appProps = msg.getApplicationProperties();

                messageId       = props.getMessageIdAsString();
                correlationId   = props.getCorrelationIdAsString();
                sourceRequestId = (String)appProps.get(HEADER_SOURCE_REQUEST_ID);
                domainName      = (String)appProps.get(HEADER_DOMAIN_NAME);
                eventName       = (String)appProps.get(HEADER_EVENT_NAME);                
            }
            
            if(GeneralHelper.isStrNotEmpty(messageId))
                mdcAttr.setMessageId(messageId);
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
