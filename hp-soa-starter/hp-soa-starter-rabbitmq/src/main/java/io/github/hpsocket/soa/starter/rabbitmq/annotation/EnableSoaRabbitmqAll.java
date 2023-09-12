package io.github.hpsocket.soa.starter.rabbitmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** <b>HP-SOA Rabbitmq 启用注解</b><br>
 * 同时启用 Producer 和 Consumer
 */
@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@EnableSoaRabbitmqProducer
@EnableSoaRabbitmqConsumer
public @interface EnableSoaRabbitmqAll
{

}
