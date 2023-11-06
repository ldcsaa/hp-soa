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

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaThirdRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaThirdRabbitmqProperties.class})
public class SoaThirdRabbitmqConfig extends SoaAbstractRabbitmqConfig
{
    public static final String rabbitConnectionDetailsBeanName = "thirdRabbitConnectionDetails";
    public static final String rabbitConnectionFactoryBeanConfigurerBeanName = "thirdRabbitConnectionFactoryBeanConfigurer";
    public static final String rabbitCredentialsProviderBeanName = "thirdRabbitCredentialsProvider";
    public static final String rabbitCredentialsRefreshServiceBeanName = "thirdRabbitCredentialsRefreshService";
    public static final String rabbitCachingConnectionFactoryConfigurerBeanName = "thirdRabbitCachingConnectionFactoryConfigurer";
    public static final String rabbitConnectionNameStrategyBeanName = "thirdRabbitConnectionNameStrategy";
    public static final String rabbitCachingConnectionFactoryBeanName = "thirdRabbitCachingConnectionFactory";
    public static final String rabbitConnectionFactoryCustomizerBeanName = "thirdRabbitConnectionFactoryCustomizer";
    public static final String rabbitAmqpAdminBeanName = "thirdRabbitAmqpAdmin";
    public static final String rabbitStreamEnvironmentBeanName = "thirdRabbitStreamEnvironment";
    public static final String rabbitStreamEnvironmentBuilderCustomizerBeanName = "thirdRabbitStreamEnvironmentBuilderCustomizer";
    public static final String rabbitStreamAdminBeanName = "thirdRabbitStreamAdmin";
    
    public SoaThirdRabbitmqConfig(SoaThirdRabbitmqProperties properties)
    {
        super(properties);
    }

    @Override
    @Bean(rabbitConnectionDetailsBeanName)
    RabbitConnectionDetails rabbitConnectionDetails() {
        return super.rabbitConnectionDetails();
    }

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
    
    @Override
    @Bean(rabbitCachingConnectionFactoryConfigurerBeanName)
    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
        @Qualifier(rabbitConnectionDetailsBeanName) RabbitConnectionDetails connectionDetails,
        @Qualifier(rabbitConnectionNameStrategyBeanName) ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
    {
        return super.rabbitConnectionFactoryConfigurer(connectionDetails, connectionNameStrategy);
    }
    
    @Override
    @Bean(rabbitCachingConnectionFactoryBeanName)
    public CachingConnectionFactory rabbitConnectionFactory(
        @Qualifier(rabbitConnectionFactoryBeanConfigurerBeanName) RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
        @Qualifier(rabbitCachingConnectionFactoryConfigurerBeanName) CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
        @Qualifier(rabbitConnectionFactoryCustomizerBeanName) ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
    {
        return super.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitCachingConnectionFactoryConfigurer, connectionFactoryCustomizers);
    }
    
    @Override
    @Bean(rabbitAmqpAdminBeanName)
    public AmqpAdmin amqpAdmin(@Qualifier(rabbitCachingConnectionFactoryBeanName) ConnectionFactory connectionFactory)
    {
        return super.amqpAdmin(connectionFactory);
    }
    
    @Override
    @Bean(name = rabbitStreamEnvironmentBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    public Environment rabbitStreamEnvironment(
        @Qualifier(rabbitStreamEnvironmentBuilderCustomizerBeanName) ObjectProvider<EnvironmentBuilderCustomizer> customizers)
    {
        return super.rabbitStreamEnvironment(customizers);
    }
    
    @Override
    @Bean(name = rabbitStreamAdminBeanName)
    @ConditionalOnMissingBean(name = rabbitStreamAdminBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-third.stream", name = "name")
    public StreamAdmin streamAdmin(@Qualifier(rabbitStreamEnvironmentBeanName) Environment env)
    {
        return super.streamAdmin(env);
    }

}
