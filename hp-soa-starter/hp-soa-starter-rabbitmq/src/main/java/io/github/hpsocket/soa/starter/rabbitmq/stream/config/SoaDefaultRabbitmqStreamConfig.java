package io.github.hpsocket.soa.starter.rabbitmq.stream.config;

import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.EnvironmentBuilderCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitStreamTemplateConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;

import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
@ConditionalOnBean({SoaRabbitmqStreamConfig.class, SoaDefaultRabbitmqProperties.class})
public class SoaDefaultRabbitmqStreamConfig extends SoaAbstractRabbitmqStreamConfig
{
	public SoaDefaultRabbitmqStreamConfig(SoaDefaultRabbitmqProperties properties)
	{
		super(properties);
	}

	@Primary
	@Override
	@Bean(name = "defaultStreamRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "stream")
	public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
		@Qualifier("defaultRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
		ObjectProvider<ConsumerCustomizer> consumerCustomizer,
		ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
	{
		return super.streamRabbitListenerContainerFactory(rabbitStreamEnvironment, consumerCustomizer, containerCustomizer);
	}

	@Primary
	@Override
	@Bean(name = "defaultRabbitStreamEnvironment")
	public Environment rabbitStreamEnvironment(
		ObjectProvider<EnvironmentBuilderCustomizer> customizers)
	{
		return super.rabbitStreamEnvironment(customizers);
	}

	@Primary
	@Override
	@Bean("defaultRabbitStreamTemplateConfigurer")
	public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<StreamMessageConverter> streamMessageConverter,
		ObjectProvider<ProducerCustomizer> producerCustomizer)
	{
		return super.rabbitStreamTemplateConfigurer(messageConverter, streamMessageConverter, producerCustomizer);
	}

	@Primary
	@Override
	@Bean("defaultRabbitStreamTemplate")
	@ConditionalOnProperty(prefix = "spring.rabbitmq.stream", name = "name")
	public RabbitStreamTemplate rabbitStreamTemplate(
		@Qualifier("defaultRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
		@Qualifier("defaultRabbitStreamTemplateConfigurer") RabbitStreamTemplateConfigurer configurer)
	{
		return super.rabbitStreamTemplate(rabbitStreamEnvironment, configurer);
	}

}
