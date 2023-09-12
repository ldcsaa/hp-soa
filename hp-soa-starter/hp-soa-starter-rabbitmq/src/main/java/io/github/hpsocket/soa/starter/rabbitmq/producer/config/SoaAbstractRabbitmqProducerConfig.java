
package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitStreamTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;

import com.rabbitmq.stream.Environment;

public class SoaAbstractRabbitmqProducerConfig
{
	protected final RabbitProperties properties;

	public SoaAbstractRabbitmqProducerConfig(RabbitProperties properties)
	{
		this.properties = properties;
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
		ObjectProvider<ReturnsCallback> returnsCallback,
		ObjectProvider<ConfirmCallback> confirmCallback,
		ObjectProvider<RecoveryCallback<?>> recoveryCallback)
	{
		RabbitTemplate template = new RabbitTemplate();
		configurer.configure(template, connectionFactory);
		customizers.orderedStream().forEach((customizer) -> customizer.customize(template));
		
		template.setReturnsCallback(returnsCallback.getIfUnique());
		template.setConfirmCallback(confirmCallback.getIfUnique());
		template.setRecoveryCallback(recoveryCallback.getIfUnique());
		
		return template;
	}

	public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate)
	{
		return new RabbitMessagingTemplate(rabbitTemplate);
	}

	public ReturnsCallback returnsCallback()
	{
		return new ReturnsCallback()
		{	
			@Override
			public void returnedMessage(ReturnedMessage returned)
			{
				
			}
		};
	}
	
	public ConfirmCallback confirmCallback()
	{
		return new ConfirmCallback()
		{	
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause)
			{
				
			}
		};
	}
	
	public <T> RecoveryCallback<T> recoveryCallback()
	{
		return new RecoveryCallback<T>()
		{
			@Override
			public T recover(RetryContext context) throws Exception
			{
				return null;
			}
		};
	}
	
	public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<StreamMessageConverter> streamMessageConverter,
		ObjectProvider<ProducerCustomizer> producerCustomizer)
	{
		RabbitStreamTemplateConfigurer configurer = new RabbitStreamTemplateConfigurer();
		configurer.setMessageConverter(messageConverter.getIfUnique());
		configurer.setStreamMessageConverter(streamMessageConverter.getIfUnique());
		configurer.setProducerCustomizer(producerCustomizer.getIfUnique());
		
		return configurer;
	}

	public RabbitStreamTemplate rabbitStreamTemplate(
		Environment rabbitStreamEnvironment,
		RabbitStreamTemplateConfigurer configurer)
	{
		RabbitStreamTemplate template = new RabbitStreamTemplate(rabbitStreamEnvironment, this.properties.getStream().getName());
		configurer.configure(template);
		
		return template;
	}

}
