
package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.EnvironmentBuilderCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.ResourceLoader;
import org.springframework.rabbit.stream.support.StreamAdmin;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;

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

    RabbitConnectionDetails rabbitConnectionDetails()
    {
        return new RabbitConnectionDetails() {

            @Override
            public String getUsername()
            {
                return properties.determineUsername();
            }

            @Override
            public String getPassword()
            {
                return properties.determinePassword();
            }

            @Override
            public String getVirtualHost()
            {
                return properties.determineVirtualHost();
            }

            @Override
            public List<Address> getAddresses()
            {
                List<Address> addresses = new ArrayList<>();
                
                for (String address : properties.determineAddresses())
                {
                    int portSeparatorIndex = address.lastIndexOf(':');
                    String host = address.substring(0, portSeparatorIndex);
                    String port = address.substring(portSeparatorIndex + 1);
                    
                    addresses.add(new Address(host, Integer.parseInt(port)));
                }
                
                return addresses;
            }
        };
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
        map.from(stream.getUsername()).as(withFallback(properties::getUsername)).whenNonNull().to(builder::username);
        map.from(stream.getPassword()).as(withFallback(properties::getPassword)).whenNonNull().to(builder::password);
        
        return builder;
    }

    private static Function<String, String> withFallback(Supplier<String> fallback)
    {
        return (value) -> (value != null) ? value : fallback.get();
    }


}
