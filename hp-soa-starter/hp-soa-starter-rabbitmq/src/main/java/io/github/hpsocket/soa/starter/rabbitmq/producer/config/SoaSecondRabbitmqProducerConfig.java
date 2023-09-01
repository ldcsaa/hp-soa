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
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RecoveryCallback;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaSecondRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqProducerConfig.class, SoaSecondRabbitmqProperties.class})
public class SoaSecondRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
	public SoaSecondRabbitmqProducerConfig(SoaSecondRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean("secondRabbitTemplateConfigurer")
	public RabbitTemplateConfigurer rabbitTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
	{
		return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
	}
	
	@Override
	@Bean("secondRabbitTemplate")
	public RabbitTemplate rabbitTemplate(
		@Qualifier("secondRabbitTemplateConfigurer") RabbitTemplateConfigurer configurer,
		@Qualifier("secondRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<RabbitTemplateCustomizer> customizers,
		@Qualifier("secondRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnsCallback,
		@Qualifier("secondRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("secondRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)
	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
	}
	
	@Override
	@Bean("secondRabbitReturnsCallback")
	@ConditionalOnMissingBean(name = "secondRabbitReturnsCallback")
	public ReturnsCallback returnsCallback()
	{
		return super.returnsCallback();
	}
	
	@Override
	@Bean("secondRabbitConfirmCallback")
	@ConditionalOnMissingBean(name = "secondRabbitConfirmCallback")
	public ConfirmCallback confirmCallback()
	{
		return super.confirmCallback();
	}
	
	@Override
	@Bean("secondRabbitRecoveryCallback")
	@ConditionalOnMissingBean(name = "secondRabbitRecoveryCallback")
	public <T> RecoveryCallback<T> recoveryCallback()
	{
		return super.recoveryCallback();
	}
	
	@Override
	@Bean("secondRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("secondRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
