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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqConsumerConfig.class, SoaFirstRabbitmqProperties.class})
public class SoaFirstRabbitmqConsumerConfig extends SoaAbstractRabbitmqConsumerConfig
{
    public static final String simpleRabbitListenerContainerFactoryConfigurerBeanName = "firstSimpleRabbitListenerContainerFactoryConfigurer";
    public static final String simpleRabbitListenerContainerFactoryBeanName = "firstSimpleRabbitListenerContainerFactory";
    public static final String rabbitCachingConnectionFactoryBeanName = "firstRabbitCachingConnectionFactory";
    public static final String rabbitSimpleContainerCustomizerBeanName = "firstRabbitSimpleContainerCustomizer";
    public static final String directRabbitListenerContainerFactoryConfigurerBeanName = "firstDirectRabbitListenerContainerFactoryConfigurer";
    public static final String directRabbitListenerContainerFactoryBeanName = "firstDirectRabbitListenerContainerFactory";
    public static final String rabbitDirectContainerCustomizerBeanName = "firstRabbitDirectContainerCustomizer";
    public static final String rabbitStreamConsumerCustomizerBeanName = "firstRabbitStreamConsumerCustomizer";
    public static final String rabbitStreamConsumerBeanName = "firstRabbitStreamConsumer";
    public static final String streamRabbitListenerContainerFactoryBeanName = "firstStreamRabbitListenerContainerFactory";
    public static final String rabbitStreamEnvironmentBeanName = "firstRabbitStreamEnvironment";
    public static final String rabbitStreamContainerCustomizerBeanName = "firstRabbitStreamContainerCustomizer";
    
    public SoaFirstRabbitmqConsumerConfig(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<MessageRecoverer> messageRecoverer,
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
        SoaFirstRabbitmqProperties properties)
    {
        super(messageConverter, messageRecoverer, retryTemplateCustomizers, properties);
    }
    
    @Override
    @Bean(simpleRabbitListenerContainerFactoryConfigurerBeanName)
    public SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer()
    {
        return super.simpleRabbitListenerContainerFactoryConfigurer();
    }
    
    @Override
    @Bean(simpleRabbitListenerContainerFactoryBeanName)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.listener", name = "type", havingValue = "simple", matchIfMissing = true)
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
        @Qualifier(simpleRabbitListenerContainerFactoryConfigurerBeanName) SimpleRabbitListenerContainerFactoryConfigurer configurer,
        @Qualifier(rabbitCachingConnectionFactoryBeanName) ConnectionFactory connectionFactory,
        @Qualifier(rabbitSimpleContainerCustomizerBeanName) ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer)
    {
        return super.simpleRabbitListenerContainerFactory(configurer, connectionFactory, simpleContainerCustomizer);
    }
    
    @Override
    @Bean(directRabbitListenerContainerFactoryConfigurerBeanName)
    public DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer()
    {
        return super.directRabbitListenerContainerFactoryConfigurer();
    }

    @Override
    @Bean(directRabbitListenerContainerFactoryBeanName)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.listener", name = "type", havingValue = "direct")
    public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
        @Qualifier(directRabbitListenerContainerFactoryConfigurerBeanName) DirectRabbitListenerContainerFactoryConfigurer configurer,
        @Qualifier(rabbitCachingConnectionFactoryBeanName) ConnectionFactory connectionFactory,
        @Qualifier(rabbitDirectContainerCustomizerBeanName) ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer)
    {
        return super.directRabbitListenerContainerFactory(configurer, connectionFactory, directContainerCustomizer);
    }
    
    @Bean(rabbitStreamConsumerCustomizerBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnMissingBean(name = rabbitStreamConsumerCustomizerBeanName)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.listener", name = "type", havingValue = "stream")
    ConsumerCustomizer consumerCustomizer()
    {
        return defaultConsumerCustomizer(rabbitStreamConsumerBeanName, OffsetSpecification.next());
    }
    
    @Override
    @Bean(streamRabbitListenerContainerFactoryBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.listener", name = "type", havingValue = "stream")
    public StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(
        @Qualifier(rabbitStreamEnvironmentBeanName) Environment rabbitStreamEnvironment,
        @Qualifier(rabbitStreamConsumerCustomizerBeanName) ObjectProvider<ConsumerCustomizer> consumerCustomizer,
        @Qualifier(rabbitStreamContainerCustomizerBeanName) ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer)
    {
        return super.streamRabbitListenerContainerFactory(rabbitStreamEnvironment, consumerCustomizer, containerCustomizer);
    }

}
