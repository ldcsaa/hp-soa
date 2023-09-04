
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerMdcInspector;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerTracingInspector;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqApplicationStartedListener;
import io.github.hpsocket.soa.starter.rabbitmq.consumer.listener.RabbitmqrSwitchReadOnlyListener;

@EnableRabbit
@AutoConfiguration
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
	@ConditionalOnClass(Trace.class)
	RabbitmqListenerTracingInspector rabbitmqListenerTracingInspector()
	{
		return new RabbitmqListenerTracingInspector();
	}	
	
	@Bean
	RabbitmqListenerMdcInspector rabbitmqListenerMdcInspector()
	{
		return new RabbitmqListenerMdcInspector();
	}
	
}
