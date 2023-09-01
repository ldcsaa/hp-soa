package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.DirectRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqConsumerConfig.class, SoaDefaultRabbitmqProperties.class})
public class SoaDefaultRabbitmqConsumerConfig extends SoaAbstractRabbitmqConsumerConfig
{
	public SoaDefaultRabbitmqConsumerConfig(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<MessageRecoverer> messageRecoverer,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
		SoaDefaultRabbitmqProperties properties)
	{
		super(messageConverter, messageRecoverer, retryTemplateCustomizers, properties);
	}
	
	@Primary
	@Override
	@Bean("defaultSimpleRabbitListenerContainerFactoryConfigurer")
	public SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer()
	{
		return super.simpleRabbitListenerContainerFactoryConfigurer();
	}
	
	@Primary
	@Override
	@Bean("defaultSimpleRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "simple", matchIfMissing = true)
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
		@Qualifier("defaultSimpleRabbitListenerContainerFactoryConfigurer") SimpleRabbitListenerContainerFactoryConfigurer configurer,
		@Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
	{
		return super.simpleRabbitListenerContainerFactory(configurer, connectionFactory, simpleContainerCustomizer);
	}
	
	@Primary
	@Override
	@Bean("defaultDirectRabbitListenerContainerFactoryConfigurer")
	public DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer()
	{
		return super.directRabbitListenerContainerFactoryConfigurer();
	}

	@Primary
	@Override
	@Bean("defaultDirectRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "direct")
	public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
		@Qualifier("defaultDirectRabbitListenerContainerFactoryConfigurer") DirectRabbitListenerContainerFactoryConfigurer configurer,
		@Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
	{
		return super.directRabbitListenerContainerFactory(configurer, connectionFactory, directContainerCustomizer);
	}
}
