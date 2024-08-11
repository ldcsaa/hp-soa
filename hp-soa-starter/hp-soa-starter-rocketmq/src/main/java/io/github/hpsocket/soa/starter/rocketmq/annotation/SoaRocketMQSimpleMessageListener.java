package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Component
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SoaRocketMQSimpleMessageListener
{
    String rocketMQTemplateBeanName() default "rocketMQClientTemplate";
}
