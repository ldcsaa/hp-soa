package io.github.hpsocket.soa.starter.data.redis.aspect;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.starter.data.redis.annotation.RateLimit;
import io.github.hpsocket.soa.starter.data.redis.annotation.RateLimits;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * <b>Redis 限流切面</b>
 */
@Slf4j
@Aspect
@Order(RateLimitAspect.ORDER)
public class RateLimitAspect
{
    public static final int ORDER = 300;
    public static final String POINTCUT_PATTERN = """
                                                    @annotation(io.github.hpsocket.soa.starter.data.redis.annotation.RateLimit)
                                                    ||
                                                    @annotation(io.github.hpsocket.soa.starter.data.redis.annotation.RateLimits)
                                                  """;

    private static final AspectHelper.AnnotationHolder<RateLimit> RATE_LIMIT_ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};
    private static final AspectHelper.AnnotationHolder<RateLimits> RATE_LIMITS_ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}

    private final RedissonClient redissonClient;

    public RateLimitAspect(RedissonClient redissonClient)
    {
        this.redissonClient = redissonClient;
    }

    @Around(value = "aroundMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        RateLimit[] rateLimitsArray = null;
        RateLimits rateLimits = RATE_LIMITS_ANNOTATION_HOLDER.findAnnotationByMethod(joinPoint);
        if(rateLimits != null)
            rateLimitsArray = rateLimits.value();
        else
        {
            RateLimit rateLimit = RATE_LIMIT_ANNOTATION_HOLDER.findAnnotationByMethod(joinPoint);
            if(rateLimit != null)
                rateLimitsArray = new RateLimit[] {rateLimit};
        }

        if(rateLimitsArray != null)
        {
            for(RateLimit limit : rateLimitsArray)
                checkRateLimit(joinPoint, limit);
        }

        return joinPoint.proceed();
    }

    private void checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit)
    {
        String limitKey = combineKey(joinPoint, rateLimit);
        if(GeneralHelper.isStrEmpty(limitKey))
            return;

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(limitKey);
        Duration duration = Duration.of(rateLimit.interval(), rateLimit.timeUnit().toChronoUnit());
        rateLimiter.trySetRate(
                    rateLimit.rateType(),
                    rateLimit.value(),
                    duration,
                    duration.multipliedBy(10));

        if(!rateLimiter.tryAcquire(1))
        {
            log.warn("Access frequency limit exceeded for key: {}", limitKey);

            String desc = GeneralHelper.isStrNotEmpty(rateLimit.desc()) ? rateLimit.desc() : ServiceException.FREQUENCY_LIMIT_EXCEPTION.getMessage();
            throw new ServiceException(desc, ServiceException.FREQUENCY_LIMIT_EXCEPTION.getStatusCode());
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
