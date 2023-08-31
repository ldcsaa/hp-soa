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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RecoveryCallback;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaFirstRabbitmqProperties.class)
@EnableConfigurationProperties(SoaFirstRabbitmqProperties.class)
public class SoaFirstRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
	public SoaFirstRabbitmqProducerConfig(SoaFirstRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean("firstRabbitTemplateConfigurer")
	public RabbitTemplateConfigurer rabbitTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
	{
		return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
	}
	
	@Override
	@Bean("firstRabbitTemplate")
	public RabbitTemplate rabbitTemplate(
		@Qualifier("firstRabbitTemplateConfigurer") RabbitTemplateConfigurer configurer,
		@Qualifier("firstRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<RabbitTemplateCustomizer> customizers,
		@Qualifier("firstRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnsCallback,
		@Qualifier("firstRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("firstRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)
	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
	}
	
	@Override
	@Bean("firstRabbitReturnsCallback")
	@ConditionalOnMissingBean(name = "firstRabbitReturnsCallback")
	public ReturnsCallback returnsCallback()
	{
		return super.returnsCallback();
	}
	
	@Override
	@Bean("firstRabbitConfirmCallback")
	@ConditionalOnMissingBean(name = "firstRabbitConfirmCallback")
	public ConfirmCallback confirmCallback()
	{
		return super.confirmCallback();
	}
	
	@Override
	@Bean("firstRabbitRecoveryCallback")
	@ConditionalOnMissingBean(name = "firstRabbitRecoveryCallback")
	public <T> RecoveryCallback<T> recoveryCallback()
	{
		return super.recoveryCallback();
	}
	
	@Override
	@Bean("firstRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("firstRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
