package io.github.hpsocket.soa.starter.rabbitmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.hpsocket.soa.starter.rabbitmq.producer.config.SoaRabbitmqProducerConfig;

/** <b>HP-SOA Rabbitmq Producer 启用注解</b><br>
 * 只启用 Producer
 */
@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Import({SoaRabbitmqProducerConfig.class})
public @interface EnableSoaRabbitmqProducer
{

}
