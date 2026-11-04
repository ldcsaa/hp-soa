
package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;
import io.github.hpsocket.soa.starter.rabbitmq.support.PropertiesRabbitConnectionDetails;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.amqp.autoconfigure.*;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.io.ResourceLoader;
import org.springframework.rabbit.stream.support.StreamAdmin;

import java.time.Duration;

public abstract class SoaAbstractRabbitmqConfig
{
    public static final ByteCapacity DEFAULT_STREAM_MAX_LENGTH_BYTES        = ByteCapacity.GB(100);
    public static final ByteCapacity DEFAULT_STREAM_MAX_SEGMENT_SIZE_BYTES  = ByteCapacity.MB(100);
    public static final Duration DEFAULT_STREAM_MAX_AGE                     = Duration.ofHours(72);
    
    protected final RabbitProperties properties;

    public SoaAbstractRabbitmqConfig(RabbitProperties properties)
    {
        this.properties = properties;
    }

    RabbitConnectionDetails rabbitConnectionDetails(ObjectProvider<SslBundles> sslBundles)
    {
        return new PropertiesRabbitConnectionDetails(properties, sslBundles.getIfAvailable());
    }

    public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(
        ResourceLoader resourceLoader, 
        RabbitConnectionDetails connectionDetails, 
        ObjectProvider<CredentialsProvider> credentialsProvider, 
        ObjectProvider<CredentialsRefreshService> credentialsRefreshService)
    {
        RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader, this.properties, connectionDetails);
        configurer.setCredentialsProvider(credentialsProvider.getIfUnique());
        configurer.setCredentialsRefreshService(credentialsRefreshService.getIfUnique());
        
        return configurer;
    }

    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(
        RabbitConnectionDetails connectionDetails, 
        ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
    {
        CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(this.properties, connectionDetails);
        configurer.setConnectionNameStrategy(connectionNameStrategy.getIfUnique());
        
        return configurer;
    }

    public CachingConnectionFactory rabbitConnectionFactory(
        RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer, 
        CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer, 
        ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception
    {

        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
        connectionFactoryBean.afterPropertiesSet();
        com.rabbitmq.client.ConnectionFactory connectionFactory = connectionFactoryBean.getObject();
        connectionFactoryCustomizers.orderedStream().forEach((customizer) -> customizer.customize(connectionFactory));

        CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
        rabbitCachingConnectionFactoryConfigurer.configure(factory);

        return factory;
    }

    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory)
    {
        return new RabbitAdmin(connectionFactory);
    }

    public Environment rabbitStreamEnvironment(
        ObjectProvider<EnvironmentBuilderCustomizer> customizers)
    {
        EnvironmentBuilder builder = configure(Environment.builder(), this.properties);
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        
        return builder.build();
    }
    
    StreamAdmin streamAdmin(Environment env)
    {
        return new StreamAdmin(env, sc ->
        {
            sc
            .stream(this.properties.getStream().getName())
            .maxAge(DEFAULT_STREAM_MAX_AGE)
            .maxLengthBytes(DEFAULT_STREAM_MAX_LENGTH_BYTES)
            .maxSegmentSizeBytes(DEFAULT_STREAM_MAX_SEGMENT_SIZE_BYTES)
            .create();
        });
    }

    protected static EnvironmentBuilder configure(EnvironmentBuilder builder, RabbitProperties properties)
    {
        builder.lazyInitialization(true);
        RabbitProperties.Stream stream = properties.getStream();
        PropertyMapper map = PropertyMapper.get();

        map.from(stream.getHost()).to(builder::host);
        map.from(stream.getPort()).to(builder::port);
        map.from(properties::getVirtualHost).to(builder::virtualHost);
        map.from(stream.getUsername()).orFrom(properties::getUsername).to(builder::username);
        map.from(stream.getPassword()).orFrom(properties::getPassword).to(builder::password);
        
        return builder;
    }

}
