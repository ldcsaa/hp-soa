package io.github.hpsocket.soa.starter.data.redis.aspect;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.starter.data.redis.annotation.RateLimit;
import io.github.hpsocket.soa.starter.data.redis.annotation.RateLimits;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * <b>Redis 限流切面</b>
 */
@Slf4j
@Aspect
@Order(RateLimitAspect.ORDER)
public class RateLimitAspect
{
    public static final int ORDER = 300;
    private final RedissonClient redissonClient;

    public RateLimitAspect(RedissonClient redissonClient)
    {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(rateLimits) || @annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimits rateLimits, RateLimit rateLimit) throws Throwable
    {
        RateLimit[] rateLimitsArray;
        if(ArrayUtils.isEmpty(rateLimits.value()))
            rateLimitsArray = new RateLimit[] {rateLimit};
        else
            rateLimitsArray = rateLimits.value();

        for(RateLimit limit : rateLimitsArray)
            checkRateLimit(joinPoint, limit);

        return joinPoint.proceed();
    }

    private void checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit)
    {
        String limitKey = combineKey(joinPoint, rateLimit);
        if(GeneralHelper.isStrEmpty(limitKey))
            return;

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(limitKey);
        rateLimiter.trySetRate(
                    rateLimit.rateType(),
                    rateLimit.rate(),
                    rateLimit.interval(),
                    rateLimit.timeUnit());

        if(!rateLimiter.tryAcquire(1))
        {
            log.warn("Access frequency limit exceeded for key: {}", limitKey);
            throw ServiceException.FREQUENCY_LIMIT_EXCEPTION;
        }
    }

    private String combineKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit)
    {
        String keyPrefix = rateLimit.keyPrefix();
        if(GeneralHelper.isStrEmpty(keyPrefix))
            keyPrefix = String.join(":", RateLimit.DEFAULT_KEY_PREFIX, AppConfigHolder.getAppName());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        StringBuilder sb = new StringBuilder(200);
        sb.append(keyPrefix).append(":").append(method.getDeclaringClass().getName()).append("#").append(method.getName());

        if(rateLimit.limitType() != RateLimit.LimitType.INTERFACE)
        {
            RequestAttribute requestAttribute = RequestContext.getRequestAttribute();
            if(requestAttribute == null)
                return null;

            if(rateLimit.limitType() == RateLimit.LimitType.USER)
            {
                Long userId = requestAttribute.getUserId();
                if(userId == null)
                    return null;

                sb.append(":user:").append(userId);
            }
            else if(rateLimit.limitType() == RateLimit.LimitType.IP)
            {
                String clientIp = requestAttribute.getClientAddr();
                if(GeneralHelper.isStrEmpty(clientIp))
                    return null;

                sb.append(":ip:").append(clientIp);
            }
        }

        return sb.toString();
    }

}
