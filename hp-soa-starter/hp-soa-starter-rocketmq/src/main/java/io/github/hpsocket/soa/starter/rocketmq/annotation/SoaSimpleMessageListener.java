package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.stereotype.Component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Component
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SoaSimpleMessageListener
{
    boolean autoAck() default true;
    String rocketMQTemplateBeanName() default RocketMQAutoConfiguration.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME;
}
