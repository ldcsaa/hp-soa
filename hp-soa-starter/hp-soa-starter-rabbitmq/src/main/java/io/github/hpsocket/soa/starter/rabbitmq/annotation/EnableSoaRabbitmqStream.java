package io.github.hpsocket.soa.starter.rabbitmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.hpsocket.soa.starter.rabbitmq.stream.config.SoaRabbitmqStreamConfig;

@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Import({SoaRabbitmqStreamConfig.class})
public @interface EnableSoaRabbitmqStream
{

}
