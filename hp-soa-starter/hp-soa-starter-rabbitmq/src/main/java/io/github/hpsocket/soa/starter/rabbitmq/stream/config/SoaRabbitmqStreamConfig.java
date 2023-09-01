
package io.github.hpsocket.soa.starter.rabbitmq.stream.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;

@AutoConfiguration
@ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
public class SoaRabbitmqStreamConfig
{

}
