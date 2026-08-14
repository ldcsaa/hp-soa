package io.github.hpsocket.soa.starter.data.redis.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/** <b>Redis 分布式锁注解</b> */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock
{
    /**
     * 锁的 Key，支持 Spring EL 表达式，例如: "'order:' + #orderId"
     */
    String key();

    /**
     * 尝试获取锁的最大等待时间（默认 5 秒，-1 表示一直等待）
     */
    long waitTime() default 5;

    /**
     * 锁自动释放时间（默认 -1，触发 Redisson 的看门狗 Watchdog 自动续期机制）
     */
    long leaseTime() default -1;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
