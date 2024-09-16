
package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitStreamTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Properties;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.BeanHelper;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class SoaAbstractRabbitmqProducerConfig
{
    protected final RabbitProperties properties;

    public SoaAbstractRabbitmqProducerConfig(RabbitProperties properties)
    {
        this.properties = properties;
    }

    public RabbitTemplateConfigurer rabbitTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter, 
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
    {
        RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().toList());
        
        return configurer;
    }

    public RabbitTemplate rabbitTemplate(
        RabbitTemplateConfigurer configurer, 
        ConnectionFactory connectionFactory, 
        ObjectProvider<RabbitTemplateCustomizer> customizers,
        ObjectProvider<ReturnsCallback> returnsCallback,
        ObjectProvider<ConfirmCallback> confirmCallback,
        ObjectProvider<RecoveryCallback<?>> recoveryCallback)
    {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        customizers.orderedStream().forEach((customizer) -> customizer.customize(template));
        
        template.setReturnsCallback(returnsCallback.getIfUnique());
        template.setConfirmCallback(confirmCallback.getIfUnique());
        template.setRecoveryCallback(recoveryCallback.getIfUnique());
        
        template.addBeforePublishPostProcessors(new MessagePostProcessor()
        {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException
            {
                injectMdcProperties(message);;

                return message;
            } 
        });
        
        return template;
    }

    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate)
    {
        return new RabbitMessagingTemplate(rabbitTemplate);
    }

    public ReturnsCallback returnsCallback()
    {
        return new ReturnsCallback()
        {    
            @Override
            public void returnedMessage(ReturnedMessage returned)
            {
                
            }
        };
    }
    
    public ConfirmCallback confirmCallback()
    {
        return new ConfirmCallback()
        {    
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause)
            {
                
            }
        };
    }
    
    public <T> RecoveryCallback<T> recoveryCallback()
    {
        return new RecoveryCallback<T>()
        {
            @Override
            public T recover(RetryContext context) throws Exception
            {
                return null;
            }
        };
    }
    
    public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
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
        Environment rabbitStreamEnvironment,
        RabbitStreamTemplateConfigurer configurer)
    {
        RabbitStreamTemplate template = new RabbitMdcStreamTemplate(rabbitStreamEnvironment, this.properties.getStream().getName());
        configurer.configure(template);
        
        return template;
    }
    
    public static class RabbitMdcStreamTemplate extends RabbitStreamTemplate
    {
        public RabbitMdcStreamTemplate(Environment environment, String streamName)
        {
            super(environment, streamName);
        }

        @Override
        public CompletableFuture<Boolean> send(Message message)
        {
            injectMdcProperties(message);
            
            return super.send(message);
        }

        @Override
        public CompletableFuture<Boolean> send(com.rabbitmq.stream.Message message)
        {
            injectMdcProperties(message);
            
            return super.send(message);
        }

    }
    
    protected static void injectMdcProperties(Message message)
    {
        WebServerHelper.assertAppIsNotReadOnly();
        
        MessageProperties messageProperties = message.getMessageProperties();
        String msgId1 = messageProperties.getMessageId();
        String msgId2 = messageProperties.getHeader(HEADER_MSG_ID);
        String msgId  = GeneralHelper.isStrEmpty(msgId1) ? (GeneralHelper.isStrEmpty(msgId2) ? IdGenerator.nextIdStr() : msgId2) : msgId1;
        String sourceRequestId = messageProperties.getHeader(HEADER_SOURCE_REQUEST_ID);
        
        if(GeneralHelper.isStrEmpty(msgId1))
            messageProperties.setMessageId(msgId);
        if(GeneralHelper.isStrEmpty(msgId2))
            messageProperties.setHeader(HEADER_MSG_ID, msgId);
        if(GeneralHelper.isStrEmpty(sourceRequestId))
            messageProperties.setHeader(HEADER_SOURCE_REQUEST_ID, MDC.get(MdcAttr.MDC_REQUEST_ID_KEY));
    }
    
    protected static void injectMdcProperties(com.rabbitmq.stream.Message message)
    {
        WebServerHelper.assertAppIsNotReadOnly();
        
        Properties props = message.getProperties();
        Map<String, Object> appProps = message.getApplicationProperties();
        
        if(appProps == null)
        {
            appProps = new HashMap<>();
            BeanHelper.setFieldValue(message, "applicationProperties", appProps);
        }

        String msgId1 = props.getMessageIdAsString();
        String msgId2 = (String)appProps.get(HEADER_MSG_ID);
        String msgId  = GeneralHelper.isStrEmpty(msgId1) ? (GeneralHelper.isStrEmpty(msgId2) ? IdGenerator.nextIdStr() : msgId2) : msgId1;
        String sourceRequestId = (String)appProps.get(HEADER_SOURCE_REQUEST_ID);
        
        if(GeneralHelper.isStrEmpty(msgId1))
            BeanHelper.setFieldValue(props, "messageId", msgId);
        if(GeneralHelper.isStrEmpty(msgId2))
            appProps.put(HEADER_MSG_ID, msgId);
        if(GeneralHelper.isStrEmpty(sourceRequestId))
            appProps.put(HEADER_SOURCE_REQUEST_ID, MDC.get(MdcAttr.MDC_REQUEST_ID_KEY));
    }
    
}
