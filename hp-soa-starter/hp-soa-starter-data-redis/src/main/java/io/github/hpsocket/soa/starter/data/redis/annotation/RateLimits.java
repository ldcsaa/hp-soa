package io.github.hpsocket.soa.starter.data.redis.annotation;

import java.lang.annotation.*;

/** <b>Redis 限流组合注解</b> */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimits
{
    /** 限流组合 */
    RateLimit[] value();
}
