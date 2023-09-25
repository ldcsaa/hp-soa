package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitStreamTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;
import org.springframework.retry.RecoveryCallback;

import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqProducerConfig.class, SoaFirstRabbitmqProperties.class})
public class SoaFirstRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
    public SoaFirstRabbitmqProducerConfig(SoaFirstRabbitmqProperties properties)
    {
        super(properties);
    }

    @Override
    @Bean("firstRabbitTemplateConfigurer")
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
    {
        return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
    }
    
    @Override
    @Bean("firstRabbitTemplate")
    public RabbitTemplate rabbitTemplate(
        @Qualifier("firstRabbitTemplateConfigurer") RabbitTemplateConfigurer configurer,
        @Qualifier("firstRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
        @Qualifier("firstRabbitTemplateCustomizer") ObjectProvider<RabbitTemplateCustomizer> customizers,
        @Qualifier("firstRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnsCallback,
        @Qualifier("firstRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
        @Qualifier("firstRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)
    {
        return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
    }
    
    @Override
    @Bean("firstRabbitReturnsCallback")
    @ConditionalOnMissingBean(name = "firstRabbitReturnsCallback")
    public ReturnsCallback returnsCallback()
    {
        return super.returnsCallback();
    }
    
    @Override
    @Bean("firstRabbitConfirmCallback")
    @ConditionalOnMissingBean(name = "firstRabbitConfirmCallback")
    public ConfirmCallback confirmCallback()
    {
        return super.confirmCallback();
    }
    
    @Override
    @Bean("firstRabbitRecoveryCallback")
    @ConditionalOnMissingBean(name = "firstRabbitRecoveryCallback")
    public <T> RecoveryCallback<T> recoveryCallback()
    {
        return super.recoveryCallback();
    }
    
    @Override
    @Bean("firstRabbitMessagingTemplate")
    public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("firstRabbitTemplate") RabbitTemplate rabbitTemplate)
    {
        return super.rabbitMessagingTemplate(rabbitTemplate);
    }
    
    @Override
    @Bean("firstRabbitStreamTemplateConfigurer")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnMissingBean(name = "firstRabbitStreamTemplateConfigurer")
    public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<StreamMessageConverter> streamMessageConverter,
        @Qualifier("firstRabbitStreamProducerCustomizer")ObjectProvider<ProducerCustomizer> producerCustomizer)
    {
        return super.rabbitStreamTemplateConfigurer(messageConverter, streamMessageConverter, producerCustomizer);
    }

    @Override
    @Bean("firstRabbitStreamTemplate")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.stream", name = "name")
    public RabbitStreamTemplate rabbitStreamTemplate(
        @Qualifier("firstRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
        @Qualifier("firstRabbitStreamTemplateConfigurer") RabbitStreamTemplateConfigurer configurer)
    {
        return super.rabbitStreamTemplate(rabbitStreamEnvironment, configurer);
    }

}
