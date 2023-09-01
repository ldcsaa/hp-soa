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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqConsumerConfig.class, SoaFirstRabbitmqProperties.class})
public class SoaFirstRabbitmqConsumerConfig extends SoaAbstractRabbitmqConsumerConfig
{
	public SoaFirstRabbitmqConsumerConfig(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<MessageRecoverer> messageRecoverer,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
		SoaFirstRabbitmqProperties properties)
	{
		super(messageConverter, messageRecoverer, retryTemplateCustomizers, properties);
	}
	
	@Override
	@Bean("firstSimpleRabbitListenerContainerFactoryConfigurer")
	public SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer()
	{
		return super.simpleRabbitListenerContainerFactoryConfigurer();
	}
	
	@Override
	@Bean("firstSimpleRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq-first.listener", name = "type", havingValue = "simple", matchIfMissing = true)
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
		@Qualifier("firstSimpleRabbitListenerContainerFactoryConfigurer") SimpleRabbitListenerContainerFactoryConfigurer configurer,
		@Qualifier("firstRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
	{
		return super.simpleRabbitListenerContainerFactory(configurer, connectionFactory, simpleContainerCustomizer);
	}
	
	@Override
	@Bean("firstDirectRabbitListenerContainerFactoryConfigurer")
	public DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer()
	{
		return super.directRabbitListenerContainerFactoryConfigurer();
	}

	@Override
	@Bean("firstDirectRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq-first.listener", name = "type", havingValue = "direct")
	public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
		@Qualifier("firstDirectRabbitListenerContainerFactoryConfigurer") DirectRabbitListenerContainerFactoryConfigurer configurer,
		@Qualifier("firstRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
	{
		return super.directRabbitListenerContainerFactory(configurer, connectionFactory, directContainerCustomizer);
	}
}
