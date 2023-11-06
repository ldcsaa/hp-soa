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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.support.StreamAdmin;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaDefaultRabbitmqProperties.class})
public class SoaDefaultRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
    public static final String rabbitConnectionDetailsBeanName = "defaultRabbitConnectionDetails";
    public static final String rabbitConnectionFactoryBeanConfigurerBeanName = "defaultRabbitConnectionFactoryBeanConfigurer";
    public static final String rabbitCredentialsProviderBeanName = "defaultRabbitCredentialsProvider";
    public static final String rabbitCredentialsRefreshServiceBeanName = "defaultRabbitCredentialsRefreshService";
    public static final String rabbitCachingConnectionFactoryConfigurerBeanName = "defaultRabbitCachingConnectionFactoryConfigurer";
    public static final String rabbitConnectionNameStrategyBeanName = "defaultRabbitConnectionNameStrategy";
    public static final String rabbitCachingConnectionFactoryBeanName = "defaultRabbitCachingConnectionFactory";
    public static final String rabbitConnectionFactoryCustomizerBeanName = "defaultRabbitConnectionFactoryCustomizer";
    public static final String rabbitAmqpAdminBeanName = "defaultRabbitAmqpAdmin";
    public static final String rabbitStreamEnvironmentBeanName = "defaultRabbitStreamEnvironment";
    public static final String rabbitStreamEnvironmentBuilderCustomizerBeanName = "defaultRabbitStreamEnvironmentBuilderCustomizer";
    public static final String rabbitStreamAdminBeanName = "defaultRabbitStreamAdmin";
    
    public SoaDefaultRabbitmqConfig(SoaDefaultRabbitmqProperties properties)
    {
        super(properties);
    }

    @Primary
    @Override
    @Bean(rabbitConnectionDetailsBeanName)
    RabbitConnectionDetails rabbitConnectionDetails() {
        return super.rabbitConnectionDetails();
    }

    @Primary
    @Override
    @Bean(rabbitConnectionFactoryBeanConfigurerBeanName)
    public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
        ResourceLoader resourceLoader,
        @Qualifier(rabbitConnectionDetailsBeanName) RabbitConnectionDetails connectionDetails,
        @Qualifier(rabbitCredentialsProviderBeanName) ObjectProvider<CredentialsProvider> credentialsProvider,
        @Qualifier(rabbitCredentialsRefreshServiceBeanName) ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
    {
        return super.rabbitConnectionFactoryBeanConfigurer(resourceLoader, connectionDetails, credentialsProvider, credentialsRefreshService);
    }
    
    @Primary
    @Override
    @Bean(rabbitCachingConnectionFactoryConfigurerBeanName)
    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
        @Qualifier(rabbitConnectionDetailsBeanName) RabbitConnectionDetails connectionDetails,
        @Qualifier(rabbitConnectionNameStrategyBeanName) ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
    {
        return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
    }
    
    @Primary
    @Override
    @Bean(rabbitCachingConnectionFactoryBeanName)
    public CachingConnectionFactory rabbitConnectionFactory(
        @Qualifier(rabbitConnectionFactoryBeanConfigurerBeanName) RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
        @Qualifier(rabbitCachingConnectionFactoryConfigurerBeanName) CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
        @Qualifier(rabbitConnectionFactoryCustomizerBeanName) ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
    {
        return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
    }
    
    @Primary
    @Override
    @Bean(rabbitAmqpAdminBeanName)
    public AmqpAdmin amqpAdmin(@Qualifier(rabbitCachingConnectionFactoryBeanName) ConnectionFactory connectionFactory)
    {
        return super.amqpAdmin(connectionFactory);
    }
    
    @Primary
    @Override
    @Bean(name = rabbitStreamEnvironmentBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    public Environment rabbitStreamEnvironment(
        @Qualifier(rabbitStreamEnvironmentBuilderCustomizerBeanName) ObjectProvider<EnvironmentBuilderCustomizer> customizers)
    {
        return super.rabbitStreamEnvironment(customizers);
    }
    
    @Primary
    @Override
    @Bean(name = rabbitStreamAdminBeanName)
    @ConditionalOnMissingBean(name = rabbitStreamAdminBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq.stream", name = "name")
    public StreamAdmin streamAdmin(@Qualifier(rabbitStreamEnvironmentBeanName) Environment env)
    {
        return super.streamAdmin(env);
    }
    
}
