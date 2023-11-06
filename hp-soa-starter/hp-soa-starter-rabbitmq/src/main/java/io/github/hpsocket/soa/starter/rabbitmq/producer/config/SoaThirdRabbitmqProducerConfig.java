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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaThirdRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqProducerConfig.class, SoaThirdRabbitmqProperties.class})
public class SoaThirdRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
    public static final String rabbitTemplateConfigurerBeanName = "thirdRabbitTemplateConfigurer";
    public static final String rabbitTemplateBeanName = "thirdRabbitTemplate";
    public static final String rabbitCachingConnectionFactoryBeanName = "thirdRabbitCachingConnectionFactory";
    public static final String rabbitTemplateCustomizerBeanName = "thirdRabbitTemplateCustomizer";
    public static final String rabbitReturnsCallbackBeanName = "thirdRabbitReturnsCallback";
    public static final String rabbitConfirmCallbackBeanName = "thirdRabbitConfirmCallback";
    public static final String rabbitRecoveryCallbackBeanName = "thirdRabbitRecoveryCallback";
    public static final String rabbitMessagingTemplateBeanName = "thirdRabbitMessagingTemplate";
    public static final String rabbitStreamTemplateConfigurerBeanName = "thirdRabbitStreamTemplateConfigurer";
    public static final String rabbitStreamProducerCustomizerBeanName = "thirdRabbitStreamProducerCustomizer";
    public static final String rabbitStreamTemplateBeanName = "thirdRabbitStreamTemplate";
    public static final String rabbitStreamEnvironmentBeanName = "thirdRabbitStreamEnvironment";
    
    public SoaThirdRabbitmqProducerConfig(SoaThirdRabbitmqProperties properties)
    {
        super(properties);
    }

    @Override
    @Bean(rabbitTemplateConfigurerBeanName)
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
    {
        return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
    }
    
    @Override
    @Bean(rabbitTemplateBeanName)
    public RabbitTemplate rabbitTemplate(
        @Qualifier(rabbitTemplateConfigurerBeanName) RabbitTemplateConfigurer configurer,
        @Qualifier(rabbitCachingConnectionFactoryBeanName) ConnectionFactory connectionFactory,
        @Qualifier(rabbitTemplateCustomizerBeanName) ObjectProvider<RabbitTemplateCustomizer> customizers,
        @Qualifier(rabbitReturnsCallbackBeanName) ObjectProvider<ReturnsCallback> returnsCallback,
        @Qualifier(rabbitConfirmCallbackBeanName) ObjectProvider<ConfirmCallback> confirmCallback,
        @Qualifier(rabbitRecoveryCallbackBeanName) ObjectProvider<RecoveryCallback<?>> recoveryCallback)
    {
        return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
    }
    
    @Override
    @Bean(rabbitReturnsCallbackBeanName)
    @ConditionalOnMissingBean(name = rabbitReturnsCallbackBeanName)
    public ReturnsCallback returnsCallback()
    {
        return super.returnsCallback();
    }
    
    @Override
    @Bean(rabbitConfirmCallbackBeanName)
    @ConditionalOnMissingBean(name = rabbitConfirmCallbackBeanName)
    public ConfirmCallback confirmCallback()
    {
        return super.confirmCallback();
    }
    
    @Override
    @Bean(rabbitRecoveryCallbackBeanName)
    @ConditionalOnMissingBean(name = rabbitRecoveryCallbackBeanName)
    public <T> RecoveryCallback<T> recoveryCallback()
    {
        return super.recoveryCallback();
    }
    
    @Override
    @Bean(rabbitMessagingTemplateBeanName)
    public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier(rabbitTemplateBeanName) RabbitTemplate rabbitTemplate)
    {
        return super.rabbitMessagingTemplate(rabbitTemplate);
    }
    
    @Override
    @Bean(rabbitStreamTemplateConfigurerBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnMissingBean(name = rabbitStreamTemplateConfigurerBeanName)
    public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<StreamMessageConverter> streamMessageConverter,
        @Qualifier(rabbitStreamProducerCustomizerBeanName)ObjectProvider<ProducerCustomizer> producerCustomizer)
    {
        return super.rabbitStreamTemplateConfigurer(messageConverter, streamMessageConverter, producerCustomizer);
    }

    @Override
    @Bean(rabbitStreamTemplateBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-third.stream", name = "name")
    public RabbitStreamTemplate rabbitStreamTemplate(
        @Qualifier(rabbitStreamEnvironmentBeanName) Environment rabbitStreamEnvironment,
        @Qualifier(rabbitStreamTemplateConfigurerBeanName) RabbitStreamTemplateConfigurer configurer)
    {
        return super.rabbitStreamTemplate(rabbitStreamEnvironment, configurer);
    }

}
