package io.github.hpsocket.soa.starter.rabbitmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@EnableSoaRabbitmqStream
@EnableSoaRabbitmqProducerAndConsumer
public @interface EnableSoaRabbitmqAll
{

}
