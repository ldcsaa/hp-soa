
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitListenerApplicationStartedListener;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitListenerSwitchReadOnlyListener;

@AutoConfiguration
//@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
public class SoaRabbitmqConsumerConfig
{
	@Bean
	@DependsOn("springContextHolder")
	RabbitListenerApplicationStartedListener rabbitListenerApplicationStartedListener()
	{
		return new RabbitListenerApplicationStartedListener();
	}
	
	@Bean
	RabbitListenerSwitchReadOnlyListener RabbitListenerSwitchReadOnlyListener()
	{
		return new RabbitListenerSwitchReadOnlyListener();
	}
	
}
