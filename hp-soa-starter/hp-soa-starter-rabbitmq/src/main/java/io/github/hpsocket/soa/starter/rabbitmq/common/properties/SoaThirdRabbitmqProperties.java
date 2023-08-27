package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty("spring.rabbitmq.third.host")
@ConfigurationProperties(prefix = "spring.rabbitmq.third")
public class SoaThirdRabbitmqProperties extends RabbitProperties
{

}
