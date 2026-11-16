package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import org.springframework.amqp.rabbit.config.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.support.micrometer.RabbitListenerObservationConvention;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.amqp.autoconfigure.*;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.micrometer.RabbitStreamListenerObservationConvention;

import java.lang.reflect.Method;
import java.util.List;

public abstract class SoaAbstractRabbitmqConsumerConfig
{
    protected final ObjectProvider<MessageConverter> messageConverter;
    protected final ObjectProvider<MessageRecoverer> messageRecoverer;
    protected final ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers;
    protected final ObjectProvider<RabbitListenerObservationConvention> observationConvention;


    protected final RabbitProperties properties;

    public SoaAbstractRabbitmqConsumerConfig(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<MessageRecoverer> messageRecoverer,
        ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers,
        ObjectProvider<RabbitListenerObservationConvention> observationConvention,
        RabbitProperties properties)
    {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retrySettingsCustomizers = retrySettingsCustomizers;
        this.observationConvention = observationConvention;
        this.properties = properties;
    }
    
    public SimpleRabbitListenerContainerFactoryConfigurer platformSimpleRabbitListenerContainerFactoryConfigurer()
    {
        return simpleListenerConfigurer();
    }

    public SimpleRabbitListenerContainerFactoryConfigurer virtualSimpleRabbitListenerContainerFactoryConfigurer()
    {
        SimpleRabbitListenerContainerFactoryConfigurer configurer = simpleListenerConfigurer();
        configurer.setTaskExecutor(new VirtualThreadTaskExecutor("rabbit-simple-"));

        return configurer;
    }

    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
        SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory,
        ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
    {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);

        return factory;
    }

    public DirectRabbitListenerContainerFactoryConfigurer platformDirectRabbitListenerContainerFactoryConfigurer()
    {
        return directListenerConfigurer();
    }

    public DirectRabbitListenerContainerFactoryConfigurer virtualDirectRabbitListenerContainerFactoryConfigurer()
    {
        DirectRabbitListenerContainerFactoryConfigurer configurer = directListenerConfigurer();
        configurer.setTaskExecutor(new VirtualThreadTaskExecutor("rabbit-direct-"));

        return configurer;
    }

    public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
        DirectRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory,
        ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
    {
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        directContainerCustomizer.ifUnique(factory::setContainerCustomizer);

        return factory;
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
        ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer,
        ObjectProvider<RabbitStreamListenerObservationConvention> observationConvention)
    {
        StreamRabbitListenerContainerFactory factory = new StreamRabbitListenerContainerFactory(rabbitStreamEnvironment);
        RabbitProperties.StreamContainer stream = properties.getListener().getStream();
        factory.setObservationEnabled(stream.isObservationEnabled());
        factory.setNativeListener(stream.isNativeListener());
        observationConvention.ifUnique(factory::setStreamListenerObservationConvention);
        consumerCustomizer.ifUnique(factory::setConsumerCustomizer);
        containerCustomizer.ifUnique(factory::setContainerCustomizer);
        
        return factory;
    }

    private SimpleRabbitListenerContainerFactoryConfigurer simpleListenerConfigurer()
    {
        SimpleRabbitListenerContainerFactoryConfigurer configurer = new SimpleRabbitListenerContainerFactoryConfigurer(this.properties);
        return listenerConfigurer(configurer);
    }

    private DirectRabbitListenerContainerFactoryConfigurer directListenerConfigurer()
    {
        DirectRabbitListenerContainerFactoryConfigurer configurer = new DirectRabbitListenerContainerFactoryConfigurer(this.properties);
        return listenerConfigurer(configurer);
    }

    private <T extends AbstractRabbitListenerContainerFactoryConfigurer> T listenerConfigurer(T configurer)
    {
        try
        {
            Method setMessageConverter = AbstractRabbitListenerContainerFactoryConfigurer.class.getDeclaredMethod("setMessageConverter", MessageConverter.class);
            setMessageConverter.setAccessible(true);
            setMessageConverter.invoke(configurer, this.messageConverter.getIfUnique());

            Method setMessageRecoverer = AbstractRabbitListenerContainerFactoryConfigurer.class.getDeclaredMethod("setMessageRecoverer", MessageRecoverer.class);
            setMessageRecoverer.setAccessible(true);
            setMessageRecoverer.invoke(configurer, this.messageRecoverer.getIfUnique());

            Method setObservationConvention = AbstractRabbitListenerContainerFactoryConfigurer.class.getDeclaredMethod("setObservationConvention", RabbitListenerObservationConvention.class);
            setObservationConvention.setAccessible(true);
            setObservationConvention.invoke(configurer, this.observationConvention.getIfUnique());

            Method setRetrySettingsCustomizers = AbstractRabbitListenerContainerFactoryConfigurer.class.getDeclaredMethod("setRetrySettingsCustomizers", List.class);
            setRetrySettingsCustomizers.setAccessible(true);
            setRetrySettingsCustomizers.invoke(configurer, this.retrySettingsCustomizers.orderedStream().toList());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return configurer;
    }

}
