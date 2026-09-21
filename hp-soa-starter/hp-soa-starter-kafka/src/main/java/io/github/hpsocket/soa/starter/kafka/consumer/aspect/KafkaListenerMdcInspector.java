package io.github.hpsocket.soa.starter.kafka.consumer.aspect;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.kafka.exception.KafkaExceptionHandler;
import io.github.hpsocket.soa.starter.kafka.util.KafkaConstant;
import io.github.hpsocket.soa.starter.kafka.util.KafkaHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.util.Iterator;

/** <b>Kafka Listener MDC 拦截器</b><br>
 * 用于注入 MDC 调用链跟踪信息
 */
@Slf4j
@Aspect
@Order(KafkaListenerMdcInspector.ORDER)
public class KafkaListenerMdcInspector
{
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = "execution (public void *.*(..)) && "
                                                + "("
                                                + "     @annotation(org.springframework.kafka.annotation.KafkaListener) || "
                                                + "     ("
                                                + "         @target(org.springframework.kafka.annotation.KafkaListener) &&"
                                                + "         @annotation(org.springframework.kafka.annotation.KafkaHandler)"
                                                + "     )"
                                                + ")";

    private static final AspectHelper.AnnotationHolder<KafkaListener> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};

    @Autowired(required = false)
    private AsyncService asyncService;
    @Autowired(required = false)
    private KafkaExceptionHandler exceptionHandler;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();        

        KafkaListener listener = ANNOTATION_HOLDER.findAnnotationByMethodOrClass(joinPoint);
        Assert.notNull(listener, "@KafkaListener annotation not found");
        
        String listenerId       = listener.id();
        String messageId        = null;
        String sourceRequestId  = null;
        String domainName       = null;
        String eventName        = null;
        String correlationId    = null;
        
        Object obj = AspectHelper.findFirstArgByTypes(joinPoint, ConsumerRecord.class, Message.class, Iterable.class);
        
        if(obj != null)
        {
            if(obj instanceof Iterable<?> itb)
            {
                Iterator<?> it = itb.iterator();
                obj = it.hasNext() ? it.next() : null;
            }
            
            if(obj instanceof Message msg)
            {
                MessageHeaders headers = msg.getHeaders();
                
                messageId       = GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_MSG_ID));
                sourceRequestId = GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_SOURCE_REQUEST_ID));
                domainName      = GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_DOMAIN_NAME));
                eventName       = GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_EVENT_NAME));
                correlationId   = GeneralHelper.bytes2Str((byte[])headers.get(KafkaConstant.HEADER_CORRELATION_ID));                    
            }
            else if(obj instanceof ConsumerRecord crc)
            {
                Headers headers = crc.headers();

                messageId       = KafkaHelper.getHeaderValue(headers, KafkaConstant.HEADER_MSG_ID);
                sourceRequestId = KafkaHelper.getHeaderValue(headers, KafkaConstant.HEADER_SOURCE_REQUEST_ID);
                domainName      = KafkaHelper.getHeaderValue(headers, KafkaConstant.HEADER_DOMAIN_NAME);
                eventName       = KafkaHelper.getHeaderValue(headers, KafkaConstant.HEADER_EVENT_NAME);
                correlationId   = KafkaHelper.getHeaderValue(headers, KafkaConstant.HEADER_CORRELATION_ID);
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
            if(log.isDebugEnabled())
            {
                log.debug("start process message -> (listenerId: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {})"
                        , listenerId, correlationId, messageId, sourceRequestId, domainName, eventName);
                
                sw = new StopWatch(listenerId);
                sw.start();
            }
            
            return joinPoint.proceed();
        }
        catch(Exception e)
        {
            invokeExceptionHandler(obj, messageId, e);
            throw e;
        }
        finally
        {
            
            if(log.isDebugEnabled())
            {
                sw.stop();

                log.debug("end process message -> (listenerId: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {}, costTime: {})"
                        , listenerId, correlationId, messageId, sourceRequestId, domainName, eventName, sw.getTotalTimeMillis());
            }

            mdcAttr.removeMdc();
        }
    }

    private void invokeExceptionHandler(Object msg, String messageId, Exception e)
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
