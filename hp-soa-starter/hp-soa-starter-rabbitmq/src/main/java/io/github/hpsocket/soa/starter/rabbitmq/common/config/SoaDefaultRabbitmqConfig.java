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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.retry.RecoveryCallback;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaDefaultRabbitmqProperties.class)
@EnableConfigurationProperties(SoaDefaultRabbitmqProperties.class)
public class SoaDefaultRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
	public SoaDefaultRabbitmqConfig(SoaDefaultRabbitmqProperties properties)
	{
		super(properties);
	}

	@Primary
	@Override
	@Bean("defaultRabbitConnectionDetails")
	RabbitConnectionDetails rabbitConnectionDetails() {
		return super.rabbitConnectionDetails();
	}

	@Primary
	@Override
	@Bean("defaultRabbitConnectionFactoryBeanConfigurer")
	public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
		ResourceLoader resourceLoader,
		@Qualifier("defaultRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<CredentialsProvider> credentialsProvider,
		ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitCachingConnectionFactoryConfigurer")
	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
		@Qualifier("defaultRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitCachingConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(
		@Qualifier("defaultRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
		@Qualifier("defaultRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
		ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{
		return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
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
	@Bean("defaultAmqpAdmin")
	public AmqpAdmin amqpAdmin(@Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
	{
		return super.amqpAdmin(connectionFactory);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("defaultRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
