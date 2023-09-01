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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaThirdRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqProducerConfig.class, SoaThirdRabbitmqProperties.class})
public class SoaThirdRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
	public SoaThirdRabbitmqProducerConfig(SoaThirdRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean("thirdRabbitTemplateConfigurer")
	public RabbitTemplateConfigurer rabbitTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
	{
		return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
	}
	
	@Override
	@Bean("thirdRabbitTemplate")
	public RabbitTemplate rabbitTemplate(
		@Qualifier("thirdRabbitTemplateConfigurer") RabbitTemplateConfigurer configurer,
		@Qualifier("thirdRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<RabbitTemplateCustomizer> customizers,
		@Qualifier("thirdRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnsCallback,
		@Qualifier("thirdRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("thirdRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)
	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
	}
	
	@Override
	@Bean("thirdRabbitReturnsCallback")
	@ConditionalOnMissingBean(name = "thirdRabbitReturnsCallback")
	public ReturnsCallback returnsCallback()
	{
		return super.returnsCallback();
	}
	
	@Override
	@Bean("thirdRabbitConfirmCallback")
	@ConditionalOnMissingBean(name = "thirdRabbitConfirmCallback")
	public ConfirmCallback confirmCallback()
	{
		return super.confirmCallback();
	}
	
	@Override
	@Bean("thirdRabbitRecoveryCallback")
	@ConditionalOnMissingBean(name = "thirdRabbitRecoveryCallback")
	public <T> RecoveryCallback<T> recoveryCallback()
	{
		return super.recoveryCallback();
	}
	
	@Override
	@Bean("thirdRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("thirdRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
