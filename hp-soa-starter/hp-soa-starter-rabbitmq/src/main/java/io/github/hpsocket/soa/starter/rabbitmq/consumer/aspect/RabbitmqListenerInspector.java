package io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

@Slf4j
@Aspect
@Order(0)
public class RabbitmqListenerInspector
{
	private static final String POINTCUT_PATTERN 	= "execution (public void *.*(..)) && "
													+ "("
													+ "		@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener) || "
													+ "		("
													+ "			@target(org.springframework.amqp.rabbit.annotation.RabbitListener) &&"
													+ "			@annotation(org.springframework.amqp.rabbit.annotation.RabbitHandler)"
													+ "		)"
													+ ")";

	@Pointcut(POINTCUT_PATTERN)
	protected void aroundMethod() {}
	
	@Around(value = "aroundMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
	{
		MdcAttr mdcAttr = WebServerHelper.createMdcAttr();		

		RabbitListener listener = AspectHelper.getMethodOrClassAnnotation(joinPoint, RabbitListener.class);
		Assert.notNull(listener, "RabbitListener annotation not found");
		
		String listenerId		= listener.id();
		String messageId		= null;
		String sourceRequestId	= null;
		String domainName		= null;
		String eventName		= null;
		
		Message msg = (Message)AspectHelper.getFindFirstArgByType(joinPoint, Message.class);
		
		if(msg != null)
		{
			MessageProperties properties = msg.getMessageProperties();
			messageId 		= properties.getMessageId();
			sourceRequestId	= properties.getHeader(HEADER_SOURCE_REQUEST_ID);
			domainName		= properties.getHeader(HEADER_DOMAIN_NAME);
			eventName		= properties.getHeader(HEADER_EVENT_NAME);
			
			if(GeneralHelper.isStrNotEmpty(messageId))
				mdcAttr.setMessageId(messageId);
		}
		
		mdcAttr.putMdc();
		
		StopWatch sw = new StopWatch(listenerId);
		
		try
		{
			log.debug("rabbit listener start consume message -> (listenerId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {})"
					, listenerId, messageId, sourceRequestId, domainName, eventName);
			
			sw.start();
			
			return joinPoint.proceed();	
		}
		finally
		{
			sw.stop();
			
			log.debug("rabbit listener end consume message -> (listenerId: {}, messageId: {}, sourceRequestId: {}, domainName: {}, eventName: {}, costTime: {})"
					, listenerId, messageId, sourceRequestId, domainName, eventName, sw.getLastTaskTimeMillis());				

			mdcAttr.removeMdc();
		}
	}
}
