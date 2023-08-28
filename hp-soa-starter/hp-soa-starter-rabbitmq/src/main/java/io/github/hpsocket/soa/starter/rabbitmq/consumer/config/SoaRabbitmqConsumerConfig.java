
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerInspector;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqApplicationStartedListener;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqrSwitchReadOnlyListener;

@AutoConfiguration
@Import(RabbitmqListenerInspector.class)
public class SoaRabbitmqConsumerConfig
{
	@Bean
	@DependsOn("springContextHolder")
	RabbitmqApplicationStartedListener rabbitmqApplicationStartedListener()
	{
		return new RabbitmqApplicationStartedListener();
	}
	
	@Bean
	RabbitmqrSwitchReadOnlyListener RabbitmqrSwitchReadOnlyListener()
	{
		return new RabbitmqrSwitchReadOnlyListener();
	}
	
}
