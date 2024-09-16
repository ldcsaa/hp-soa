package io.github.hpsocket.soa.starter.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.stereotype.Component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Simple Consumer 消息接收监听器注解 */
@Component
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SoaSimpleMessageListener
{
    /** 是否自动确认消息：如果为 true 则不需要手工调用 {@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#ack(org.apache.rocketmq.client.apis.message.MessageView) ack(MessageView messageView)}，否则需要手工调用该方法确认消息 */
    boolean autoAck() default true;
    /** 监听目标 {@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate RocketMQClientTemplate} bean 名称，默认值：{@linkplain org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration#ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME "rocketMQClientTemplate"} */
    String rocketMQTemplateBeanName() default RocketMQAutoConfiguration.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME;
}
