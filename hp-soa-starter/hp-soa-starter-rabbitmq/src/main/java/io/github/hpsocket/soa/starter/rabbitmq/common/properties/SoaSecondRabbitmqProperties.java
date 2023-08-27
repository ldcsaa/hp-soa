package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty("spring.rabbitmq.second.host")
@ConfigurationProperties(prefix = "spring.rabbitmq.second")
public class SoaSecondRabbitmqProperties extends RabbitProperties
{

}
