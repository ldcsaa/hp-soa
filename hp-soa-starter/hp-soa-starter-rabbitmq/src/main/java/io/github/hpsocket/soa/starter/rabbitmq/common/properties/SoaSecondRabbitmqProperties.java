package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq.second")
@ConditionalOnExpression("'${spring.rabbitmq.second.host:}' != '' || '${spring.rabbitmq.second.addresses:}' != ''")
public class SoaSecondRabbitmqProperties extends RabbitProperties
{
	public SoaSecondRabbitmqProperties()
	{
		this.setHost(null);
		this.setAddresses(null);
	}
}
