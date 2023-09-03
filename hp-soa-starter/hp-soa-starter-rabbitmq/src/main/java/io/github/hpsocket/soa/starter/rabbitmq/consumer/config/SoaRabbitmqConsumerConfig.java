
package io.github.hpsocket.soa.starter.rabbitmq.consumer.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.starter.rabbitmq.consumer.aspect.RabbitmqListenerInspector;

@EnableRabbit
@AutoConfiguration
@Import(RabbitmqListenerInspector.class)
public class SoaRabbitmqConsumerConfig
{
	
}
