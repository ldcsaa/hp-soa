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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;

import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaSecondRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean(SoaSecondRabbitmqProperties.class)
@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
@EnableConfigurationProperties(SoaSecondRabbitmqProperties.class)
public class SoaSecondRabbitmqStreamConfig extends SoaAbstractRabbitmqStreamConfig
{
	public SoaSecondRabbitmqStreamConfig(SoaSecondRabbitmqProperties properties)
	{
		super(properties);
	}

	@Override
	@Bean(name = "secondStreamRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq.second.listener", name = "type", havingValue = "stream")
	public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
		@Qualifier("secondRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
		ObjectProvider<ConsumerCustomizer> consumerCustomizer,
		ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
	{
		return super.streamRabbitListenerContainerFactory(rabbitStreamEnvironment, consumerCustomizer, containerCustomizer);
	}

	@Override
	@Bean(name = "secondRabbitStreamEnvironment")
	public Environment rabbitStreamEnvironment(
		ObjectProvider<EnvironmentBuilderCustomizer> customizers)
	{
		return super.rabbitStreamEnvironment(customizers);
	}

	@Override
	@Bean("secondRabbitStreamTemplateConfigurer")
	public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<StreamMessageConverter> streamMessageConverter,
		ObjectProvider<ProducerCustomizer> producerCustomizer)
	{
		return super.rabbitStreamTemplateConfigurer(messageConverter, streamMessageConverter, producerCustomizer);
	}

	@Override
	@Bean("secondRabbitStreamTemplate")
	@ConditionalOnProperty(prefix = "spring.rabbitmq.second.stream", name = "name")
	public RabbitStreamTemplate rabbitStreamTemplate(
		@Qualifier("secondRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
		@Qualifier("secondRabbitStreamTemplateConfigurer") RabbitStreamTemplateConfigurer configurer)
	{
		return super.rabbitStreamTemplate(rabbitStreamEnvironment, configurer);
	}

}
