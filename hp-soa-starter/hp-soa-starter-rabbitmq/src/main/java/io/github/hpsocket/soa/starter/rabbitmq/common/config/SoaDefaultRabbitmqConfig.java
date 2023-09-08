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
import org.springframework.boot.autoconfigure.amqp.EnvironmentBuilderCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.support.StreamAdmin;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaDefaultRabbitmqProperties.class})
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
		@Qualifier("defaultRabbitCredentialsProvider") ObjectProvider<CredentialsProvider> credentialsProvider,
		@Qualifier("defaultRabbitCredentialsRefreshService") ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitCachingConnectionFactoryConfigurer")
	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
		@Qualifier("defaultRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		@Qualifier("defaultRabbitConnectionNameStrategy") ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
	}
	
	@Primary
	@Override
	@Bean("defaultRabbitCachingConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(
		@Qualifier("defaultRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
		@Qualifier("defaultRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
		@Qualifier("defaultRabbitConnectionFactoryCustomizer") ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{
		return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
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
	@Bean(name = "defaultRabbitStreamEnvironment")
	@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
	public Environment rabbitStreamEnvironment(
		@Qualifier("defaultRabbitStreamEnvironmentBuilderCustomizer") ObjectProvider<EnvironmentBuilderCustomizer> customizers)
	{
		return super.rabbitStreamEnvironment(customizers);
	}
	
	@Primary
	@Override
	@Bean(name = "defaultStreamAdmin")
	@ConditionalOnMissingBean(name = "defaultStreamAdmin")
	@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
	@ConditionalOnProperty(prefix = "spring.rabbitmq.stream", name = "name")
	public StreamAdmin streamAdmin(@Qualifier("defaultRabbitStreamEnvironment") Environment env)
	{
		return super.streamAdmin(env);
	}
	
}
