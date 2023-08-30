
package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
			SoaDefaultRabbitmqProducerConfig.class,
			SoaFirstRabbitmqProducerConfig.class,
			SoaSecondRabbitmqProducerConfig.class,
			SoaThirdRabbitmqProducerConfig.class
})
public class SoaRabbitmqProducerConfig
{
	
}
