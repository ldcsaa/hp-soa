
package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.retry.RecoveryCallback;

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

	public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate)
	{
		return new RabbitMessagingTemplate(rabbitTemplate);
	}

}
