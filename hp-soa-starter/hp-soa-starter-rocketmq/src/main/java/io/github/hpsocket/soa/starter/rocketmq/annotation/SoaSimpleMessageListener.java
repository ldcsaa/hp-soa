package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import cn.hutool.core.annotation.AliasFor;

@Component
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SoaSimpleMessageListener
{
    boolean autoAck() default true;
    @AliasFor(attribute = "value")
    String rocketMQTemplateBeanName() default "rocketMQClientTemplate";
    @AliasFor(attribute = "rocketMQTemplateBeanName")
    String value() default  "rocketMQClientTemplate";
}
