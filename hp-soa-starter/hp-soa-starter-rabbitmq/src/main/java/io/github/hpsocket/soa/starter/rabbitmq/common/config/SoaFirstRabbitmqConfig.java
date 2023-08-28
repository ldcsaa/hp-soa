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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaFirstRabbitmqProperties.class)
@EnableConfigurationProperties(SoaFirstRabbitmqProperties.class)
public class SoaFirstRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
	public SoaFirstRabbitmqConfig(SoaFirstRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean("firstRabbitConnectionDetails")
	RabbitConnectionDetails rabbitConnectionDetails()
	{
		return super.rabbitConnectionDetails();
	}

	@Override
	@Bean("firstRabbitConnectionFactoryBeanConfigurer")
	public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
		ResourceLoader resourceLoader,
		@Qualifier("firstRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<CredentialsProvider> credentialsProvider,
		ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
	}
	
	@Override
	@Bean("firstRabbitCachingConnectionFactoryConfigurer")
	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
		@Qualifier("firstRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
	}
	
	@Override
	@Bean("firstRabbitCachingConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(
		@Qualifier("firstRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
		@Qualifier("firstRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
		ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{
		return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
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
		@Qualifier("firstRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnCallback,
		@Qualifier("firstRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("firstRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)

	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnCallback, confirmCallback, recoveryCallback);
	}
	
	@Override
	@Bean("firstAmqpAdmin")
	public AmqpAdmin amqpAdmin(@Qualifier("firstRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
	{
		return super.amqpAdmin(connectionFactory);
	}
	
	@Override
	@Bean("firstRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("firstRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
