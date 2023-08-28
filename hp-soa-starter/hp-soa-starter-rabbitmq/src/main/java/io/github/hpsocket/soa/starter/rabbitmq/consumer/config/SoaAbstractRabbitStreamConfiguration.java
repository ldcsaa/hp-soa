package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.EnvironmentBuilderCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitStreamTemplateConfigurer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;

public class SoaAbstractRabbitStreamConfiguration
{
	public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
		Environment rabbitStreamEnvironment,
		RabbitProperties properties,
		ObjectProvider<ConsumerCustomizer> consumerCustomizer,
		ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
	{
		StreamRabbitListenerContainerFactory factory = new StreamRabbitListenerContainerFactory(rabbitStreamEnvironment);
		factory.setNativeListener(properties.getListener().getStream().isNativeListener());
		consumerCustomizer.ifUnique(factory::setConsumerCustomizer);
		containerCustomizer.ifUnique(factory::setContainerCustomizer);
		
		return factory;
	}

	public Environment rabbitStreamEnvironment(
		RabbitProperties properties,
		ObjectProvider<EnvironmentBuilderCustomizer> customizers)
	{
		EnvironmentBuilder builder = configure(Environment.builder(), properties);
		customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
		
		return builder.build();
	}

	public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
		RabbitProperties properties,
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
		Environment rabbitStreamEnvironment, RabbitProperties properties,
		RabbitStreamTemplateConfigurer configurer)
	{
		RabbitStreamTemplate template = new RabbitStreamTemplate(rabbitStreamEnvironment, properties.getStream().getName());
		configurer.configure(template);
		
		return template;
	}

	static EnvironmentBuilder configure(EnvironmentBuilder builder, RabbitProperties properties)
	{
		builder.lazyInitialization(true);
		RabbitProperties.Stream stream = properties.getStream();
		PropertyMapper map = PropertyMapper.get();
		
		map.from(stream.getHost()).to(builder::host);
		map.from(stream.getPort()).to(builder::port);
		map.from(stream.getUsername()).as(withFallback(properties::getUsername)).whenNonNull().to(builder::username);
		map.from(stream.getPassword()).as(withFallback(properties::getPassword)).whenNonNull().to(builder::password);
		
		return builder;
	}

	private static Function<String, String> withFallback(Supplier<String> fallback)
	{
		return (value) -> (value != null) ? value : fallback.get();
	}

}
