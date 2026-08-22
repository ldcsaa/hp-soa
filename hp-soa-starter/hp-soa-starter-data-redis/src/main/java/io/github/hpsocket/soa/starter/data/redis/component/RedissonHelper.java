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

    public void tryLockRun(String lockKey, long waitTime, TimeUnit timeUnit, Runnable runnable)
    {
        tryLockRun(lockKey, waitTime, -1, timeUnit, runnable);
    }

    public void tryLockRun(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Runnable runnable)
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

    public void lockRun(String lockKey, Runnable runnable)
    {
        lockRun(lockKey, -1, TimeUnit.SECONDS, runnable);
    }

    public void lockRun(String lockKey, long leaseTime, TimeUnit timeUnit, Runnable runnable)
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

    public <T> T tryLockCall(String lockKey, long waitTime, TimeUnit timeUnit, Callable<T> callable)
    {
        return tryLockCall(lockKey, waitTime, -1, timeUnit, callable);
    }

    public <T> T tryLockCall(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Callable<T> callable)
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

    public <T> T lockCall(String lockKey, Callable<T> callable)
    {
        return lockCall(lockKey, -1, TimeUnit.SECONDS, callable);
    }

    public <T> T lockCall(String lockKey, long leaseTime, TimeUnit timeUnit, Callable<T> callable)
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
