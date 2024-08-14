package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SoaSimpleConsumerReceiveConfiguration
{
    String AUTO_START_PLACEHOLDER               = "${rocketmq.simple-consumer.receive.auto-start:}";
    String CONSUMPTION_THREAD_COUNT_PLACEHOLDER = "${rocketmq.simple-consumer.receive.consumption-thread-count:}";
    String MAX_MESSAGE_NUM_PLACEHOLDER          = "${rocketmq.simple-consumer.receive.max-message-num:}";
    String INVISIBLE_DURATION_PLACEHOLDER       = "${rocketmq.simple-consumer.receive.invisible-duration:}";
    
    String autoStart() default AUTO_START_PLACEHOLDER;
    String consumptionThreadCount() default CONSUMPTION_THREAD_COUNT_PLACEHOLDER;
    String maxMessageNum() default MAX_MESSAGE_NUM_PLACEHOLDER;
    String invisibleDuration() default INVISIBLE_DURATION_PLACEHOLDER;
}
