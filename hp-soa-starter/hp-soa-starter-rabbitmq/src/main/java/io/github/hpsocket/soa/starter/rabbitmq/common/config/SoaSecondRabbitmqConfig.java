package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

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
	@Bean("secondAmqpAdmin")
	public AmqpAdmin amqpAdmin(@Qualifier("secondRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
	{
		return super.amqpAdmin(connectionFactory);
	}
}
