package io.github.hpsocket.soa.starter.rabbitmq.common.properties;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq.third")
@ConditionalOnExpression("'${spring.rabbitmq.third.host:}' != '' || '${spring.rabbitmq.third.addresses:}' != ''")
public class SoaThirdRabbitmqProperties extends RabbitProperties
{
	public SoaThirdRabbitmqProperties()
	{
		this.setHost(null);
		this.setAddresses(null);
	}
}
