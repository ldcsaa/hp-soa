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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaThirdRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaThirdRabbitmqProperties.class)
@EnableConfigurationProperties(SoaThirdRabbitmqProperties.class)
public class SoaThirdRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
	public SoaThirdRabbitmqConfig(SoaThirdRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean("thirdRabbitConnectionDetails")
	RabbitConnectionDetails rabbitConnectionDetails()
	{
		return super.rabbitConnectionDetails();
	}

	@Override
	@Bean("thirdRabbitConnectionFactoryBeanConfigurer")
	public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
		ResourceLoader resourceLoader,
		@Qualifier("thirdRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<CredentialsProvider> credentialsProvider,
		ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
	}
	
	@Override
	@Bean("thirdRabbitCachingConnectionFactoryConfigurer")
	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
		@Qualifier("thirdRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
	}
	
	@Override
	@Bean("thirdRabbitCachingConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(
		@Qualifier("thirdRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
		@Qualifier("thirdRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
		ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{
		return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
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
		@Qualifier("thirdRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnCallback,
		@Qualifier("thirdRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
		@Qualifier("thirdRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)

	{
		return super.rabbitTemplate(configurer, connectionFactory, customizers, returnCallback, confirmCallback, recoveryCallback);
	}
	
	@Override
	@Bean("thirdAmqpAdmin")
	public AmqpAdmin amqpAdmin(@Qualifier("thirdRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
	{
		return super.amqpAdmin(connectionFactory);
	}
	
	@Override
	@Bean("thirdRabbitMessagingTemplate")
	public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("thirdRabbitTemplate") RabbitTemplate rabbitTemplate)
	{
		return super.rabbitMessagingTemplate(rabbitTemplate);
	}
}
