package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
@ConditionalOnExpression("'${spring.rabbitmq.host:}' != '' || '${spring.rabbitmq.addresses:}' != ''")
public class SoaDefaultRabbitmqProperties extends RabbitProperties
{

}
