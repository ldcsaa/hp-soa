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
import org.springframework.boot.autoconfigure.amqp.DirectRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;

public class SoaAbstractRabbitAnnotationDrivenConfiguration
{
	protected final ObjectProvider<MessageConverter> messageConverter;
	protected final ObjectProvider<MessageRecoverer> messageRecoverer;
	protected final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

	protected final RabbitProperties properties;

	public SoaAbstractRabbitAnnotationDrivenConfiguration(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<MessageRecoverer> messageRecoverer,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
		RabbitProperties properties)
	{
		this.messageConverter = messageConverter;
		this.messageRecoverer = messageRecoverer;
		this.retryTemplateCustomizers = retryTemplateCustomizers;
		this.properties = properties;
	}
	
	public SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer()
	{
		SimpleRabbitListenerContainerFactoryConfigurer configurer = new SimpleRabbitListenerContainerFactoryConfigurer(this.properties);
		
		/*
		configurer.setMessageConverter(this.messageConverter.getIfUnique());
		configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
		configurer.setRetryTemplateCustomizers(this.retryTemplateCustomizers.orderedStream().toList());
		*/
		
		return configurer;
	}
	
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
		SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory,
		ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
	{
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setMessageConverter(this.messageConverter.getIfUnique());
		configurer.configure(factory, connectionFactory);
		simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
		
		return factory;
	}
	
	public DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer()
	{
		DirectRabbitListenerContainerFactoryConfigurer configurer = new DirectRabbitListenerContainerFactoryConfigurer(this.properties);
		
		/*
		configurer.setMessageConverter(this.messageConverter.getIfUnique());
		configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
		configurer.setRetryTemplateCustomizers(this.retryTemplateCustomizers.orderedStream().toList());
		*/

		return configurer;
	}

	public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
		DirectRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory,
		ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
	{
		DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
		factory.setMessageConverter(this.messageConverter.getIfUnique());
		configurer.configure(factory, connectionFactory);
		directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
		
		return factory;
	}
}
