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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaThirdRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqConsumerConfig.class, SoaThirdRabbitmqProperties.class})
public class SoaThirdRabbitmqConsumerConfig extends SoaAbstractRabbitmqConsumerConfig
{
	public SoaThirdRabbitmqConsumerConfig(
		ObjectProvider<MessageConverter> messageConverter,
		ObjectProvider<MessageRecoverer> messageRecoverer,
		ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
		SoaThirdRabbitmqProperties properties)
	{
		super(messageConverter, messageRecoverer, retryTemplateCustomizers, properties);
	}
	
	@Override
	@Bean("thirdSimpleRabbitListenerContainerFactoryConfigurer")
	public SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer()
	{
		return super.simpleRabbitListenerContainerFactoryConfigurer();
	}
	
	@Override
	@Bean("thirdSimpleRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq-third.listener", name = "type", havingValue = "simple", matchIfMissing = true)
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
		@Qualifier("thirdSimpleRabbitListenerContainerFactoryConfigurer") SimpleRabbitListenerContainerFactoryConfigurer configurer,
		@Qualifier("thirdRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		@Qualifier("thirdRabbitSimpleContainerCustomizer") ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
	{
		return super.simpleRabbitListenerContainerFactory(configurer, connectionFactory, simpleContainerCustomizer);
	}
	
	@Override
	@Bean("thirdDirectRabbitListenerContainerFactoryConfigurer")
	public DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer()
	{
		return super.directRabbitListenerContainerFactoryConfigurer();
	}

	@Override
	@Bean("thirdDirectRabbitListenerContainerFactory")
	@ConditionalOnProperty(prefix = "spring.rabbitmq-third.listener", name = "type", havingValue = "direct")
	public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
		@Qualifier("thirdDirectRabbitListenerContainerFactoryConfigurer") DirectRabbitListenerContainerFactoryConfigurer configurer,
		@Qualifier("thirdRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
		@Qualifier("thirdRabbitDirectContainerCustomizer") ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
	{
		return super.directRabbitListenerContainerFactory(configurer, connectionFactory, directContainerCustomizer);
	}
	
	@Bean(name = "thirdRabbitStreamConsumerCustomizer")
	@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
	@ConditionalOnMissingBean(name = "thirdRabbitStreamConsumerCustomizer")
	@ConditionalOnProperty(prefix = "spring.rabbitmq-third.listener", name = "type", havingValue = "stream")
	ConsumerCustomizer consumerCustomizer()
	{
		return defaultConsumerCustomizer("thirdRabbitStreamConsumer", OffsetSpecification.next());
	}
	
	@Override
	@Bean(name = "thirdStreamRabbitListenerContainerFactory")
	@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
	@ConditionalOnProperty(prefix = "spring.rabbitmq-third.listener", name = "type", havingValue = "stream")
	public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
		@Qualifier("thirdRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
		@Qualifier("thirdRabbitStreamConsumerCustomizer") ObjectProvider<ConsumerCustomizer> consumerCustomizer,
		@Qualifier("thirdRabbitStreamContainerCustomizer") ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
	{
		return super.streamRabbitListenerContainerFactory(rabbitStreamEnvironment, consumerCustomizer, containerCustomizer);
	}

}
