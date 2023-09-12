package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import java.time.Duration;
import java.util.List;

import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.DirectRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.ListenerRetry;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

public class SoaAbstractRabbitmqConsumerConfig
{
	protected final ObjectProvider<MessageConverter> messageConverter;
	protected final ObjectProvider<MessageRecoverer> messageRecoverer;
	protected final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

	protected final RabbitProperties properties;

	public SoaAbstractRabbitmqConsumerConfig(
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
		parseRetryConfig(factory);
		
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
		parseRetryConfig(factory);
		
		directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
		
		return factory;
	}
	
	protected void parseRetryConfig(AbstractRabbitListenerContainerFactory<?> factory)
	{
		ListenerRetry retryConfig = this.properties.getListener().getSimple().getRetry();
		
		if (retryConfig.isEnabled())
		{
			RetryInterceptorBuilder<?, ?> builder = (retryConfig.isStateless())
													? RetryInterceptorBuilder.stateless()
													: RetryInterceptorBuilder.stateful();
			RetryTemplate retryTemplate = createRetryTemplate(retryConfig, RabbitRetryTemplateCustomizer.Target.LISTENER);
			builder.retryOperations(retryTemplate);
			
			MessageRecoverer recoverer = this.messageRecoverer.getIfUnique();
			
			if(recoverer == null)
				recoverer = new RejectAndDontRequeueRecoverer();
			
			builder.recoverer(recoverer);
			factory.setAdviceChain(builder.build());
		}
	}
	
	private RetryTemplate createRetryTemplate(RabbitProperties.Retry properties, RabbitRetryTemplateCustomizer.Target target)
	{
		PropertyMapper map = PropertyMapper.get();
		RetryTemplate template = new RetryTemplate();
		SimpleRetryPolicy policy = new SimpleRetryPolicy();
		
		map.from(properties::getMaxAttempts).to(policy::setMaxAttempts);
		template.setRetryPolicy(policy);
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		map.from(properties::getInitialInterval)
			.whenNonNull()
			.as(Duration::toMillis)
			.to(backOffPolicy::setInitialInterval);
		map.from(properties::getMultiplier).to(backOffPolicy::setMultiplier);
		map.from(properties::getMaxInterval).whenNonNull().as(Duration::toMillis).to(backOffPolicy::setMaxInterval);
		template.setBackOffPolicy(backOffPolicy);
		
		List<RabbitRetryTemplateCustomizer> customizers = this.retryTemplateCustomizers.orderedStream().toList();
		
		if (GeneralHelper.isNotNullOrEmpty(customizers))
		{
			for (RabbitRetryTemplateCustomizer customizer : customizers)
			{
				customizer.customize(target, template);
			}
		}
		
		return template;
	}
	
	public ConsumerCustomizer defaultConsumerCustomizer(String name, OffsetSpecification offset)
	{
		return (id, builder) -> {
			builder.name(name)
			.offset(offset)
			.autoTrackingStrategy();
		};
	}
	
	public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
		Environment rabbitStreamEnvironment,
		ObjectProvider<ConsumerCustomizer> consumerCustomizer,
		ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
	{
		StreamRabbitListenerContainerFactory factory = new StreamRabbitListenerContainerFactory(rabbitStreamEnvironment);
		factory.setNativeListener(this.properties.getListener().getStream().isNativeListener());
		consumerCustomizer.ifUnique(factory::setConsumerCustomizer);
		containerCustomizer.ifUnique(factory::setContainerCustomizer);
		
		return factory;
	}
}
