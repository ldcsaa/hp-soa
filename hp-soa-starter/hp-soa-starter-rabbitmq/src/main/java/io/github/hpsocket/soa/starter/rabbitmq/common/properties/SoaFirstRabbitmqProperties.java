package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq.first")
@ConditionalOnExpression("'${spring.rabbitmq.first.host:}' != '' || '${spring.rabbitmq.first.addresses:}' != ''")
public class SoaFirstRabbitmqProperties extends RabbitProperties
{
	public SoaFirstRabbitmqProperties()
	{
		this.setHost(null);
		this.setAddresses(null);
	}
}
