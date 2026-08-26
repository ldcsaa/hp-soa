package io.github.hpsocket.soa.starter.data.redis.aspect;

import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.starter.data.redis.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;

import java.util.concurrent.TimeUnit;

/**
 * <b>Redis 分布式锁切面</b>
 */
@Slf4j
@Aspect
@Order(DistributedLockAspect.ORDER)
public class DistributedLockAspect
{
    public static final int ORDER = 500;
    private final RedissonClient redissonClient;

    public DistributedLockAspect(RedissonClient redissonClient)
    {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable
    {
        long waitTime     = distributedLock.waitTime();
        long leaseTime    = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();
        String lockKey    = AspectHelper.parseExpressionValue(distributedLock.key(), joinPoint, String.class);
        RLock lock        = redissonClient.getLock(lockKey);
        boolean isLocked  = false;

        try
        {
            isLocked = lock.tryLock(waitTime, leaseTime > 0 ? leaseTime : -1, timeUnit);

            if(!isLocked)
            {
                String msg = String.format("get distributed lock fail -> lockKey: %s", lockKey);

                log.error(msg);
                throw new RuntimeException(msg);
            }

            return joinPoint.proceed();

        }
        finally
        {
            if(isLocked && lock.isHeldByCurrentThread())
            {
                lock.unlock();
            }
        }
    }
}
