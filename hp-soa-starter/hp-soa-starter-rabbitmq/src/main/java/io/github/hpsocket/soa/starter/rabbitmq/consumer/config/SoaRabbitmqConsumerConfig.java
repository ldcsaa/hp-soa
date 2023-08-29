
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerInspector;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqApplicationStartedListener;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqrSwitchReadOnlyListener;

@AutoConfiguration
@Import({
			SoaDefaultRabbitAnnotationDrivenConfiguration.class,
			SoaFirstRabbitAnnotationDrivenConfiguration.class,
			SoaSecondRabbitAnnotationDrivenConfiguration.class,
			SoaThirdRabbitAnnotationDrivenConfiguration.class,
			
			SoaDefaultRabbitStreamConfiguration.class,
			SoaFirstRabbitStreamConfiguration.class,
			SoaSecondRabbitStreamConfiguration.class,
			SoaThirdRabbitStreamConfiguration.class,
		})
public class SoaRabbitmqConsumerConfig
{
	@Bean
	@DependsOn("springContextHolder")
	RabbitmqApplicationStartedListener rabbitmqApplicationStartedListener()
	{
		return new RabbitmqApplicationStartedListener();
	}
	
	@Bean
	RabbitmqrSwitchReadOnlyListener rabbitmqrSwitchReadOnlyListener()
	{
		return new RabbitmqrSwitchReadOnlyListener();
	}
	
	@Bean
	RabbitmqListenerInspector rabbitmqListenerInspector()
	{
		return new RabbitmqListenerInspector();
	}
	
}
