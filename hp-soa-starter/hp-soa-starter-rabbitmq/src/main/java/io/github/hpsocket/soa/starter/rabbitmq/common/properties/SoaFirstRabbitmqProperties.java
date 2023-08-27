package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty("spring.rabbitmq.first.host")
@ConfigurationProperties(prefix = "spring.rabbitmq.first")
public class SoaFirstRabbitmqProperties extends RabbitProperties
{

}
