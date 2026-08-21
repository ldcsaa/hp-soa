package io.github.hpsocket.soa.starter.data.redis.component;

import lombok.Getter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <b>Redisson 工具类</b>
 */
@Getter
public class RedissonHelper
{

    private final RedissonClient redissonClient;

    public RedissonHelper(RedissonClient redissonClient)
    {
        this.redissonClient = redissonClient;
    }

    public void tryRun(String lockKey, long waitTime, TimeUnit timeUnit, Runnable runnable)
    {
        tryRun(lockKey, waitTime, -1, timeUnit, runnable);
    }

    public void tryRun(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Runnable runnable)
    {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try
        {
            isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if(isLocked)
            {
                runnable.run();
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if(isLocked && lock.isHeldByCurrentThread())
            {
                lock.unlock();
            }
        }
    }

    public void run(String lockKey, Runnable runnable)
    {
        run(lockKey, -1, TimeUnit.SECONDS, runnable);
    }

    public void run(String lockKey, long leaseTime, TimeUnit timeUnit, Runnable runnable)
    {
        RLock lock = redissonClient.getLock(lockKey);

        try
        {
            lock.lock(leaseTime, timeUnit);
            runnable.run();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if(lock.isHeldByCurrentThread())
            {
                lock.unlock();
            }
        }
    }

    public <T> T tryCall(String lockKey, long waitTime, TimeUnit timeUnit, Callable<T> callable)
    {
        return tryCall(lockKey, waitTime, -1, timeUnit, callable);
    }

    public <T> T tryCall(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Callable<T> callable)
    {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try
        {
            isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if(isLocked)
            {
                return callable.call();
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if(isLocked && lock.isHeldByCurrentThread())
            {
                lock.unlock();
            }
        }

        return null;
    }

    public <T> T call(String lockKey, Callable<T> callable)
    {
        return call(lockKey, -1, TimeUnit.SECONDS, callable);
    }

    public <T> T call(String lockKey, long leaseTime, TimeUnit timeUnit, Callable<T> callable)
    {
        RLock lock = redissonClient.getLock(lockKey);

        try
        {
            lock.lock(leaseTime, timeUnit);
            return callable.call();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if(lock.isHeldByCurrentThread())
            {
                lock.unlock();
            }
        }
    }

}
