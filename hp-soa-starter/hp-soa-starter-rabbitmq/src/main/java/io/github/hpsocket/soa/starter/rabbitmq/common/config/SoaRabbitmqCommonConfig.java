package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.starter.rabbitmq.common.converter.FastJsonMessageConverter;
import io.github.hpsocket.soa.starter.rabbitmq.common.converter.TextMessageConverter;
import io.github.hpsocket.soa.starter.rabbitmq.common.listener.RabbitmqApplicationStartedListener;
import io.github.hpsocket.soa.starter.rabbitmq.common.listener.RabbitmqrSwitchReadOnlyListener;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerInspector;

@AutoConfiguration
@Import({
			SoaDefaultRabbitmqConfig.class,
			SoaFirstRabbitmqConfig.class,
			SoaSecondRabbitmqConfig.class,
			SoaThirdRabbitmqConfig.class
		})
public class SoaRabbitmqCommonConfig
{
	@Bean
	@DependsOn("springContextHolder")
	RabbitmqApplicationStartedListener rabbitmqApplicationStartedListener()
	{
		return new RabbitmqApplicationStartedListener();
	}
	
	@Bean
	RabbitmqrSwitchReadOnlyListener rabbitmqrSwitchReadOnlyListener()
	{
		return new RabbitmqrSwitchReadOnlyListener();
	}
	
	@Bean
	RabbitmqListenerInspector rabbitmqListenerInspector()
	{
		return new RabbitmqListenerInspector();
	}
	
	@Bean
	MessageConverter fastJsonMessageConverter()
	{
		MessageConverter textConverter = new TextMessageConverter();
		MessageConverter fastJsonConverter = new FastJsonMessageConverter();
		ContentTypeDelegatingMessageConverter delegatingConverter = new ContentTypeDelegatingMessageConverter(fastJsonConverter);
		
		delegatingConverter.addDelegate(MessageProperties.CONTENT_TYPE_JSON_ALT, fastJsonConverter);
		delegatingConverter.addDelegate(MessageProperties.CONTENT_TYPE_JSON, fastJsonConverter);
		delegatingConverter.addDelegate("text/json", fastJsonConverter);
		delegatingConverter.addDelegate("json", fastJsonConverter);
		
		delegatingConverter.addDelegate(MessageProperties.CONTENT_TYPE_TEXT_PLAIN, textConverter);
		delegatingConverter.addDelegate(MessageProperties.CONTENT_TYPE_XML, textConverter);
		delegatingConverter.addDelegate("text", textConverter);
		
		return delegatingConverter;
	}
}
