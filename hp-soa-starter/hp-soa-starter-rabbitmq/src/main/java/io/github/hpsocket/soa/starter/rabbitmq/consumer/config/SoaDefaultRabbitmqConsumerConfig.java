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
import org.springframework.context.annotation.Primary;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqConsumerConfig.class, SoaDefaultRabbitmqProperties.class})
public class SoaDefaultRabbitmqConsumerConfig extends SoaAbstractRabbitmqConsumerConfig
{
    public SoaDefaultRabbitmqConsumerConfig(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<MessageRecoverer> messageRecoverer,
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
        SoaDefaultRabbitmqProperties properties)
    {
        super(messageConverter, messageRecoverer, retryTemplateCustomizers, properties);
    }
    
    @Primary
    @Override
    @Bean("defaultSimpleRabbitListenerContainerFactoryConfigurer")
    public SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer()
    {
        return super.simpleRabbitListenerContainerFactoryConfigurer();
    }
    
    @Primary
    @Override
    @Bean("defaultSimpleRabbitListenerContainerFactory")
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "simple", matchIfMissing = true)
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
        @Qualifier("defaultSimpleRabbitListenerContainerFactoryConfigurer") SimpleRabbitListenerContainerFactoryConfigurer configurer,
        @Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
        @Qualifier("defaultRabbitSimpleContainerCustomizer") ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
    {
        return super.simpleRabbitListenerContainerFactory(configurer, connectionFactory, simpleContainerCustomizer);
    }
    
    @Primary
    @Override
    @Bean("defaultDirectRabbitListenerContainerFactoryConfigurer")
    public DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer()
    {
        return super.directRabbitListenerContainerFactoryConfigurer();
    }

    @Primary
    @Override
    @Bean("defaultDirectRabbitListenerContainerFactory")
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "direct")
    public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
        @Qualifier("defaultDirectRabbitListenerContainerFactoryConfigurer") DirectRabbitListenerContainerFactoryConfigurer configurer,
        @Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
        @Qualifier("defaultRabbitDirectContainerCustomizer") ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
    {
        return super.directRabbitListenerContainerFactory(configurer, connectionFactory, directContainerCustomizer);
    }
    
    @Primary
    @Bean(name = "defaultRabbitStreamConsumerCustomizer")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnMissingBean(name = "defaultRabbitStreamConsumerCustomizer")
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "stream")
    ConsumerCustomizer consumerCustomizer()
    {
        return defaultConsumerCustomizer("defaultRabbitStreamConsumer", OffsetSpecification.next());
    }
    
    @Primary
    @Override
    @Bean(name = "defaultStreamRabbitListenerContainerFactory")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "stream")
    public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
        @Qualifier("defaultRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
        @Qualifier("defaultRabbitStreamConsumerCustomizer") ObjectProvider<ConsumerCustomizer> consumerCustomizer,
        @Qualifier("defaultRabbitStreamContainerCustomizer") ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
    {
        return super.streamRabbitListenerContainerFactory(rabbitStreamEnvironment, consumerCustomizer, containerCustomizer);
    }

}
