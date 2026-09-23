package io.github.hpsocket.soa.starter.data.redis.annotation;

import org.redisson.api.RateType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/** <b>Redis 限流注解</b> */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit
{
    /** 默认限流 Key 前缀 */
    String DEFAULT_KEY_PREFIX = "rate_limit";

    /** 限流 Key 前缀（默认：rate_limit:${APP_NAME}） */
    String keyPrefix() default "";

    /** 限流描述（默认：系统繁忙） */
    String desc() default "";

    /** 限流值 */
    long value();

    /** 时间窗口（默认：1） */
    long interval() default 1;

    /** 时间单位（默认：秒） */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /** 限流维度类型 */
    LimitType limitType() default LimitType.INTERFACE;

    /** 限流范围类型（OVERALL: 全局限流，PER_CLIENT: 客户端实例限流） */
    RateType rateType() default RateType.OVERALL;

    enum LimitType
    {
        /** 接口维度限流 */
        INTERFACE,
        /** 客户端 IP 限维度流 */
        IP,
        /** 用户维度限流（需要在 Request 中包含 userId/Token） */
        USER
    }

}
