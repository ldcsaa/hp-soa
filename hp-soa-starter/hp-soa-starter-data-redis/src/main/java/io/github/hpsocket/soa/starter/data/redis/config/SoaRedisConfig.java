package io.github.hpsocket.soa.starter.data.redis.config;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.data.redis.aspect.DistributedLockAspect;
import io.github.hpsocket.soa.starter.data.redis.consumer.aspect.RedisListenerMdcInspector;
import io.github.hpsocket.soa.starter.data.redis.consumer.aspect.RedisListenerTracingInspector;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

/** <b>HP-SOA Redis 公共配置</b> */
@AutoConfiguration
@EnableCaching(order = 0)
@ComponentScan("io.github.hpsocket.soa.starter.data.redis.properties")
public class SoaRedisConfig
{
    @Value("${hp.soa.data.redis.distributed-lock.redisson-client-name:}")
    private String redissonClientName;

    @Bean
    @ConditionalOnClass(Trace.class)
    RedisListenerTracingInspector RedisListenerTracingInspector()
    {
        return new RedisListenerTracingInspector();
    }

    @Bean
    RedisListenerMdcInspector RedisListenerMdcInspector()
    {
        return new RedisListenerMdcInspector();
    }

    @Bean
    @DependsOn(SpringContextHolder.springContextHolderBeanName)
    DistributedLockAspect distributedLockAspect()
    {
        RedissonClient redissonClient = null;

        if(GeneralHelper.isStrNotEmpty(redissonClientName))
            redissonClient = SpringContextHolder.getBean(redissonClientName, RedissonClient.class);
        else
            redissonClient = SpringContextHolder.getBean(RedissonClient.class);

        return new DistributedLockAspect(redissonClient);
    }
}
