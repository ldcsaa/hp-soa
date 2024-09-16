package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Simple Consumer 消息接收配置注解 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SoaSimpleConsumerReceiveConfiguration
{
    String AUTO_START_PLACEHOLDER               = "${rocketmq.simple-consumer.receive.auto-start:}";
    String CONSUMPTION_THREAD_COUNT_PLACEHOLDER = "${rocketmq.simple-consumer.receive.consumption-thread-count:}";
    String MAX_MESSAGE_NUM_PLACEHOLDER          = "${rocketmq.simple-consumer.receive.max-message-num:}";
    String INVISIBLE_DURATION_PLACEHOLDER       = "${rocketmq.simple-consumer.receive.invisible-duration:}";
    
    /** 是否自动启动消息接收 */
    String autoStart() default AUTO_START_PLACEHOLDER;
    /** 消息接收线程数：{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) receive(int maxMessageNum, Duration invisibleDuration)} 并发线程数 */
    String consumptionThreadCount() default CONSUMPTION_THREAD_COUNT_PLACEHOLDER;
    /** 最大消息数量：{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) receive(int maxMessageNum, Duration invisibleDuration)} 的 maxMessageNum 参数 */
    String maxMessageNum() default MAX_MESSAGE_NUM_PLACEHOLDER;
    /** 消息不可见期限（秒）：{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) receive(int maxMessageNum, Duration invisibleDuration)} 的 invisibleDuration 参数 */
    String invisibleDuration() default INVISIBLE_DURATION_PLACEHOLDER;
}
