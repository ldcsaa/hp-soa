package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RecoveryCallback;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaDefaultRabbitmqProperties.class)
@EnableConfigurationProperties(SoaDefaultRabbitmqProperties.class)
public class SoaDefaultRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
	public SoaDefaultRabbitmqProducerConfig(SoaDefaultRabbitmqProperties properties)
	{
		super(properties);
	}

	@Primary
	@Override
	@Bean("defaultRabbitTemplateConfigurer")
	public RabbitTemplateConfigurer rabbitTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
	{
		return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitTemplate")
	public RabbitTemplate rabbitTemplate(
		@Qualifier("defaultRabbitTemplateConfigurer") RabbitTemplateConfigurer configurer,
		@Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<RabbitTemplateCustomizer> customizers,
		@Qualifier("defaultRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnCallback,
		@Qualifier("defaultRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("defaultRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)

	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnCallback, confirmCallback, recoveryCallback);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("defaultRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
