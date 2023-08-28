
package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.core.io.ResourceLoader;
import org.springframework.retry.RecoveryCallback;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;

public class SoaAbstractRabbitmqConfig
{
	protected final RabbitProperties properties;

	public SoaAbstractRabbitmqConfig(RabbitProperties properties)
	{
		this.properties = properties;
	}

	RabbitConnectionDetails rabbitConnectionDetails()
	{
		return new RabbitConnectionDetails() {

			@Override
			public String getUsername()
			{
				return properties.determineUsername();
			}

			@Override
			public String getPassword()
			{
				return properties.determinePassword();
			}

			@Override
			public String getVirtualHost()
			{
				return properties.determineVirtualHost();
			}

			@Override
			public List<Address> getAddresses()
			{
				List<Address> addresses = new ArrayList<>();
				for(String address : properties.determineAddresses().split(","))
				{
					String[] components = address.split(":");
					addresses.add(new Address(components[0], Integer.parseInt(components[1])));
				}
				return addresses;
			}
		};
	}

	public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
					ResourceLoader resourceLoader, 
					RabbitConnectionDetails connectionDetails, 
					ObjectProvider<CredentialsProvider> credentialsProvider, 
					ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
	{
		RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader, this.properties, connectionDetails);
		configurer.setCredentialsProvider(credentialsProvider.getIfUnique());
		configurer.setCredentialsRefreshService(credentialsRefreshService.getIfUnique());
		
		return configurer;
	}

	public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
					RabbitConnectionDetails connectionDetails, 
					ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
	{
		CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(this.properties, connectionDetails);
		configurer.setConnectionNameStrategy(connectionNameStrategy.getIfUnique());
		
		return configurer;
	}

	public CachingConnectionFactory rabbitConnectionFactory(
					RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer, 
					CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer, 
					ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
	{

		RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
		rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
		connectionFactoryBean.afterPropertiesSet();
		com.rabbitmq.client.ConnectionFactory connectionFactory = connectionFactoryBean.getObject();
		connectionFactoryCustomizers.orderedStream().forEach((customizer) -> customizer.customize(connectionFactory));

		CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
		rabbitCachingConnectionFactoryConfigurer.configure(factory);

		return factory;
	}

	public RabbitTemplateConfigurer rabbitTemplateConfigurer(
					ObjectProvider<MessageConverter> messageConverter, 
					ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
	{
		RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
		configurer.setMessageConverter(messageConverter.getIfUnique());
		configurer.setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().toList());
		
		return configurer;
	}

	public RabbitTemplate rabbitTemplate(
					RabbitTemplateConfigurer configurer, 
					ConnectionFactory connectionFactory, 
					ObjectProvider<RabbitTemplateCustomizer> customizers,
					ObjectProvider<ReturnsCallback> returnCallback,
					ObjectProvider<ConfirmCallback> confirmCallback,
					ObjectProvider<RecoveryCallback<?>> recoveryCallback)
	{
		RabbitTemplate template = new RabbitTemplate();
		configurer.configure(template, connectionFactory);
		customizers.orderedStream().forEach((customizer) -> customizer.customize(template));
		
		template.setReturnsCallback(returnCallback.getIfUnique());
		template.setConfirmCallback(confirmCallback.getIfUnique());
		template.setRecoveryCallback(recoveryCallback.getIfUnique());
		
		return template;
	}

	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory)
	{
		return new RabbitAdmin(connectionFactory);
	}

	public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate)
	{
		return new RabbitMessagingTemplate(rabbitTemplate);
	}

}
