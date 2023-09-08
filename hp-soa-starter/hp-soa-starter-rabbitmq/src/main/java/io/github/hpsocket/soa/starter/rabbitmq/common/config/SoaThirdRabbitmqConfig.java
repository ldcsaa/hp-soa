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
import org.springframework.core.io.ResourceLoader;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.support.StreamAdmin;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaThirdRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaThirdRabbitmqProperties.class})
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
		@Qualifier("thirdRabbitCredentialsProvider") ObjectProvider<CredentialsProvider> credentialsProvider,
		@Qualifier("thirdRabbitCredentialsRefreshService") ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
	}
	
	@Override
	@Bean("thirdRabbitCachingConnectionFactoryConfigurer")
	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
		@Qualifier("thirdRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
		@Qualifier("thirdRabbitConnectionNameStrategy") ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
	}
	
	@Override
	@Bean("thirdRabbitCachingConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(
		@Qualifier("thirdRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
		@Qualifier("thirdRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
		@Qualifier("thirdRabbitConnectionFactoryCustomizer") ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{
		return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
	}
	
	@Override
	@Bean("thirdAmqpAdmin")
	public AmqpAdmin amqpAdmin(@Qualifier("thirdRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
	{
		return super.amqpAdmin(connectionFactory);
	}
	
	@Override
	@Bean(name = "thirdRabbitStreamEnvironment")
	@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
	public Environment rabbitStreamEnvironment(
		@Qualifier("thirdRabbitStreamEnvironmentBuilderCustomizer") ObjectProvider<EnvironmentBuilderCustomizer> customizers)
	{
		return super.rabbitStreamEnvironment(customizers);
	}
	
	@Override
	@Bean(name = "thirdStreamAdmin")
	@ConditionalOnMissingBean(name = "thirdStreamAdmin")
	@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
	@ConditionalOnProperty(prefix = "spring.rabbitmq-third.stream", name = "name")
	public StreamAdmin streamAdmin(@Qualifier("thirdRabbitStreamEnvironment") Environment env)
	{
		return super.streamAdmin(env);
	}

}
