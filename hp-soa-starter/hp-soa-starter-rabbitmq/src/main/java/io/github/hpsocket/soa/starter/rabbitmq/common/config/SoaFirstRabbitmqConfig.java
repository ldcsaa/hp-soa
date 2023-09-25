package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.EnvironmentBuilderCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.support.StreamAdmin;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaFirstRabbitmqProperties.class})
public class SoaFirstRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
    public SoaFirstRabbitmqConfig(SoaFirstRabbitmqProperties properties)
    {
        super(properties);
    }

    @Override
    @Bean("firstRabbitConnectionDetails")
    RabbitConnectionDetails rabbitConnectionDetails()
    {
        return super.rabbitConnectionDetails();
    }

    @Override
    @Bean("firstRabbitConnectionFactoryBeanConfigurer")
    public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
        ResourceLoader resourceLoader,
        @Qualifier("firstRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
        @Qualifier("firstRabbitCredentialsProvider") ObjectProvider<CredentialsProvider> credentialsProvider,
        @Qualifier("firstRabbitCredentialsRefreshService") ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
    {
        return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
    }
    
    @Override
    @Bean("firstRabbitCachingConnectionFactoryConfigurer")
    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
        @Qualifier("firstRabbitConnectionDetails") RabbitConnectionDetails connectionDetails,
        @Qualifier("firstRabbitConnectionNameStrategy") ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
    {
        return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
    }
    
    @Override
    @Bean("firstRabbitCachingConnectionFactory")
    public CachingConnectionFactory rabbitConnectionFactory(
        @Qualifier("firstRabbitConnectionFactoryBeanConfigurer") RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
        @Qualifier("firstRabbitCachingConnectionFactoryConfigurer") CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
        @Qualifier("firstRabbitConnectionFactoryCustomizer") ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
    {
        return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
    }
    
    @Override
    @Bean("firstAmqpAdmin")
    public AmqpAdmin amqpAdmin(@Qualifier("firstRabbitCachingConnectionFactory") ConnectionFactory connectionFactory)
    {
        return super.amqpAdmin(connectionFactory);
    }
    
    @Override
    @Bean(name = "firstRabbitStreamEnvironment")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    public Environment rabbitStreamEnvironment(
        @Qualifier("firstRabbitStreamEnvironmentBuilderCustomizer") ObjectProvider<EnvironmentBuilderCustomizer> customizers)
    {
        return super.rabbitStreamEnvironment(customizers);
    }
    
    @Override
    @Bean(name = "firstStreamAdmin")
    @ConditionalOnMissingBean(name = "firstStreamAdmin")
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.stream", name = "name")
    public StreamAdmin streamAdmin(@Qualifier("firstRabbitStreamEnvironment") Environment env)
    {
        return super.streamAdmin(env);
    }

}
