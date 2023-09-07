package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import io.github.hpsocket.soa.starter.rabbitmq.common.converter.FastJsonMessageConverter;
import io.github.hpsocket.soa.starter.rabbitmq.common.converter.TextMessageConverter;

@AutoConfiguration
@ComponentScan(basePackages = "io.github.hpsocket.soa.starter.rabbitmq.common.properties")
public class SoaRabbitmqCommonConfig
{
	@Primary
	@Bean("messageConverter")
	@ConditionalOnMissingBean(name = "messageConverter")
	MessageConverter messageConverter()
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
