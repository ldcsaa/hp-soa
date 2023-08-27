package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.rabbitmq.common.converter.FastJsonMessageConverter;

@AutoConfiguration
public class SoaCommonRabbitmqConfig
{
	@Bean
	MessageConverter fastJsonMessageConverter()
	{
		return new FastJsonMessageConverter();
	}
}
