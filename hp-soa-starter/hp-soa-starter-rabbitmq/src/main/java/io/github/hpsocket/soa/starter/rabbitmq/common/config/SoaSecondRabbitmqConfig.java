package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.retry.RecoveryCallback;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaSecondRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaSecondRabbitmqProperties.class)
@EnableConfigurationProperties(SoaSecondRabbitmqProperties.class)
public class SoaSecondRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
	public SoaSecondRabbitmqConfig(SoaSecondRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean("secondRabbitConnectionDetails")
	RabbitConnectionDetails rabbitConnectionDetails()
	{
		return super.rabbitConnectionDetails();
	}

	@Override
	@Bean("secondRabbitConnectionFactoryBeanConfigurer")
	public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
		ResourceLoader resourceLoader,
		@Qualifier("secondRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<CredentialsProvider> credentialsProvider,
		ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
	}
	
	@Override
	@Bean("secondRabbitCachingConnectionFactoryConfigurer")
	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
		@Qualifier("secondRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
	}
	
	@Override
	@Bean("secondRabbitCachingConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(
		@Qualifier("secondRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
		@Qualifier("secondRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
		ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{
		return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
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
		@Qualifier("secondRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnCallback,
		@Qualifier("secondRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("secondRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)

	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnCallback, confirmCallback, recoveryCallback);
	}
	
	@Override
	@Bean("secondAmqpAdmin")
	public AmqpAdmin amqpAdmin(@Qualifier("secondRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
	{
		return super.amqpAdmin(connectionFactory);
	}
	
	@Override
	@Bean("secondRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("secondRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
