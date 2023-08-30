
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
			SoaDefaultRabbitmqConsumerConfig.class,
			SoaFirstRabbitmqConsumerConfig.class,
			SoaSecondRabbitmqConsumerConfig.class,
			SoaThirdRabbitmqConsumerConfig.class,
		})
public class SoaRabbitmqConsumerConfig
{
	
}
