package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaDefaultRabbitmqProperties;

@AutoConfiguration
@EnableConfigurationProperties(SoaDefaultRabbitmqProperties.class)
public class SoaDefaultRabbitmqConfig
{

}
