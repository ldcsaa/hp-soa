package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.rocketmq.client.annotation.ExtConsumerResetConfiguration;
import org.springframework.stereotype.Component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.apache.rocketmq.client.annotation.ExtConsumerResetConfiguration.*;

import cn.hutool.core.annotation.AliasFor;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@ExtConsumerResetConfiguration
public @interface SoaExtConsumerResetConfiguration
{
    String AUTO_CONSUME_PLACEHOLDER             = "${rocketmq.simple-consumer.auto-consume:}";
    String CONSUMPTION_THREAD_COUNT_PLACEHOLDER = "${rocketmq.simple-consumer.consumption-thread-count:}";
    String MAX_MESSAGE_NUM_PLACEHOLDER          = "${rocketmq.simple-consumer.max-message-num:}";
    String INVISIBLE_DURATION_PLACEHOLDER       = "${rocketmq.simple-consumer.invisible-duration:}";
    
    boolean autoConsume() default true;
    int consumptionThreadCount() default 1;
    int maxMessageNum() default 16;
    int invisibleDuration() default 15;

    /**
     * The component name of the Consumer configuration.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String value() default "";

    /**
     * The property of "access-key".
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String accessKey() default ACCESS_KEY_PLACEHOLDER;

    /**
     * The property of "secret-key".
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String secretKey() default SECRET_KEY_PLACEHOLDER;

    /**
     * Tag of consumer.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String tag() default TAG_PLACEHOLDER;

    /**
     * Topic name of consumer.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String topic() default TOPIC_PLACEHOLDER;

    /**
     * The access point that the SDK should communicate with.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String endpoints() default ENDPOINTS_PLACEHOLDER;

    /**
     * The load balancing group for the simple consumer.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String consumerGroup() default CONSUMER_GROUP_PLACEHOLDER;

    /**
     * The type of filter expression
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String filterExpressionType() default FILTER_EXPRESSION_TYPE_PLACEHOLDER;

    /**
     * The requestTimeout of client,it is 3s by default.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    int requestTimeout() default 3;

    /**
     * The max await time when receive messages from the server.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    int awaitDuration() default 5;

    /**
     * The namespace of consumer.
     */
    @AliasFor(annotation = ExtConsumerResetConfiguration.class)
    String namespace() default "";

}
