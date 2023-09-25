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
import org.springframework.context.annotation.Primary;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;
import org.springframework.retry.RecoveryCallback;

import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqProducerConfig.class, SoaDefaultRabbitmqProperties.class})
public class SoaDefaultRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
    public SoaDefaultRabbitmqProducerConfig(SoaDefaultRabbitmqProperties properties)
    {
        super(properties);
    }

    @Primary
    @Override
    @Bean("defaultRabbitTemplateConfigurer")
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
    {
        return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
    }
    
    @Primary
    @Override
    @Bean("defaultRabbitTemplate")
    public RabbitTemplate rabbitTemplate(
        @Qualifier("defaultRabbitTemplateConfigurer") RabbitTemplateConfigurer configurer,
        @Qualifier("defaultRabbitCachingConnectionFactory") ConnectionFactory connectionFactory,
        @Qualifier("defaultRabbitTemplateCustomizer") ObjectProvider<RabbitTemplateCustomizer> customizers,
        @Qualifier("defaultRabbitReturnsCallback") ObjectProvider<ReturnsCallback> returnsCallback,
        @Qualifier("defaultRabbitConfirmCallback") ObjectProvider<ConfirmCallback> confirmCallback,
        @Qualifier("defaultRabbitRecoveryCallback") ObjectProvider<RecoveryCallback<?>> recoveryCallback)
    {
        return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
    }
    
    @Primary
    @Override
    @Bean("defaultRabbitReturnsCallback")
    @ConditionalOnMissingBean(name = "defaultRabbitReturnsCallback")
    public ReturnsCallback returnsCallback()
    {
        return super.returnsCallback();
    }
    
    @Primary
    @Override
    @Bean("defaultRabbitConfirmCallback")
    @ConditionalOnMissingBean(name = "defaultRabbitConfirmCallback")
    public ConfirmCallback confirmCallback()
    {
        return super.confirmCallback();
    }
    
    @Primary
    @Override
    @Bean("defaultRabbitRecoveryCallback")
    @ConditionalOnMissingBean(name = "defaultRabbitRecoveryCallback")
    public <T> RecoveryCallback<T> recoveryCallback()
    {
        return super.recoveryCallback();
    }
    
    @Primary
    @Override
    @Bean("defaultRabbitMessagingTemplate")
    public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier("defaultRabbitTemplate") RabbitTemplate rabbitTemplate)
    {
        return super.rabbitMessagingTemplate(rabbitTemplate);
    }
    
    @Primary
    @Override
    @Bean("defaultRabbitStreamTemplateConfigurer")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnMissingBean(name = "defaultRabbitStreamTemplateConfigurer")
    public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<StreamMessageConverter> streamMessageConverter,
        @Qualifier("defaultRabbitStreamProducerCustomizer")ObjectProvider<ProducerCustomizer> producerCustomizer)
    {
        return super.rabbitStreamTemplateConfigurer(messageConverter, streamMessageConverter, producerCustomizer);
    }

    @Primary
    @Override
    @Bean("defaultRabbitStreamTemplate")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq.stream", name = "name")
    public RabbitStreamTemplate rabbitStreamTemplate(
        @Qualifier("defaultRabbitStreamEnvironment") Environment rabbitStreamEnvironment,
        @Qualifier("defaultRabbitStreamTemplateConfigurer") RabbitStreamTemplateConfigurer configurer)
    {
        return super.rabbitStreamTemplate(rabbitStreamEnvironment, configurer);
    }

}
