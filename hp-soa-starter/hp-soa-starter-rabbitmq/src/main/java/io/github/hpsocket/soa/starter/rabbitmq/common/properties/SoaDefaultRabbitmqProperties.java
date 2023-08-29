package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component()
@ConditionalOnProperty("spring.rabbitmq.host")
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class SoaDefaultRabbitmqProperties extends RabbitProperties
{

}
