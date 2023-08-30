
package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.io.ResourceLoader;

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

	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory)
	{
		return new RabbitAdmin(connectionFactory);
	}

}
