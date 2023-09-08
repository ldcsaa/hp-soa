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

@Slf4j
@Aspect
@Order(0)
public class RabbitmqListenerMdcInspector
{
	static final String INSPECTOR_POINTCUT_PATTERN 	= "execution (public void *.*(..)) && "
													+ "("
													+ "		@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener) || "
													+ "		("
													+ "			@target(org.springframework.amqp.rabbit.annotation.RabbitListener) &&"
													+ "			@annotation(org.springframework.amqp.rabbit.annotation.RabbitHandler)"
													+ "		)"
													+ ")";

	@Pointcut(INSPECTOR_POINTCUT_PATTERN)
	protected void aroundMethod() {}
	
	@Around(value = "aroundMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
	{
		MdcAttr mdcAttr = WebServerHelper.createMdcAttr();		

		RabbitListener listener = AspectHelper.getMethodOrClassAnnotation(joinPoint, RabbitListener.class);
		Assert.notNull(listener, "RabbitListener annotation not found");
		
		String listenerId		= listener.id();
		String correlationId	= null;
		String messageId		= null;
		String sourceRequestId	= null;
		String domainName		= null;
		String eventName		= null;
		
		Message msg = (Message)AspectHelper.getFindFirstArgByType(joinPoint, Message.class);
		
		if(msg != null)
		{
			MessageProperties properties = msg.getMessageProperties();
			messageId 		= properties.getMessageId();
			correlationId	= properties.getCorrelationId();
			sourceRequestId	= properties.getHeader(HEADER_SOURCE_REQUEST_ID);
			domainName		= properties.getHeader(HEADER_DOMAIN_NAME);
			eventName		= properties.getHeader(HEADER_EVENT_NAME);
		}
		else
		{
			org.springframework.messaging.Message<?> msg2 = (org.springframework.messaging.Message<?>)AspectHelper.getFindFirstArgByType(joinPoint, org.springframework.messaging.Message.class);
			
			if(msg2 != null)
			{
				MessageHeaders headers = msg2.getHeaders();
				messageId 		= (String)headers.get(HEADER_MSG_ID);
				correlationId	= (String)headers.get(HEADER_CORRELA_DATA_ID);
				sourceRequestId	= (String)headers.get(HEADER_SOURCE_REQUEST_ID);
				domainName		= (String)headers.get(HEADER_DOMAIN_NAME);
				eventName		= (String)headers.get(HEADER_EVENT_NAME);
			}
			else
			{
				com.rabbitmq.stream.Message msg3 = (com.rabbitmq.stream.Message)AspectHelper.getFindFirstArgByType(joinPoint, com.rabbitmq.stream.Message.class);
				
				if(msg3 != null)
				{
					Properties props = msg3.getProperties();
					Map<String, Object> appProps = msg3.getApplicationProperties();

					messageId		= props.getMessageIdAsString();
					correlationId	= props.getCorrelationIdAsString();
					sourceRequestId	= (String)appProps.get(HEADER_SOURCE_REQUEST_ID);
					domainName		= (String)appProps.get(HEADER_DOMAIN_NAME);
					eventName		= (String)appProps.get(HEADER_EVENT_NAME);
				}
			}
		}
		
		if(GeneralHelper.isStrNotEmpty(messageId))
			mdcAttr.setMessageId(messageId);
		if(GeneralHelper.isStrNotEmpty(sourceRequestId))
			mdcAttr.setSourceRequestId(sourceRequestId);
		
		mdcAttr.putMdc();
		
		StopWatch sw = new StopWatch(listenerId);
		
		try
		{
			if(log.isTraceEnabled())
				log.trace("rabbit listener start consume message -> (listenerId: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {})"
					, listenerId, correlationId, messageId, sourceRequestId, domainName, eventName);
			
			sw.start();
			
			return joinPoint.proceed();
		}
		finally
		{
			sw.stop();
			
			if(log.isTraceEnabled())
				log.trace("rabbit listener end consume message -> (listenerId: {}, correlationId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {}, costTime: {})"
					, listenerId, correlationId, messageId, sourceRequestId, domainName, eventName, sw.getLastTaskTimeMillis());				

			mdcAttr.removeMdc();
		}
	}
}
