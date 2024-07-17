
package io.github.hpsocket.soa.starter.job.exclusive.config;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.SchedulingTaskExecutor;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.data.redis.config.SoaRedisConfig;
import io.github.hpsocket.soa.starter.job.exclusive.aspect.ExclusiveJobInspector;

/** <b>HP-SOA 分布式独占 Job 配置</b> */
@AutoConfiguration
@AutoConfigureAfter(SoaRedisConfig.class)
@ConditionalOnBean({SchedulingTaskExecutor.class, RedissonClient.class})
public class SoaExclusiveJobConfig
{
    @Value("${hp.soa.job.exclusive.redisson-client-name:}")
    private String redissonClientName;

    @Bean
    @DependsOn("springContextHolder")
    ExclusiveJobInspector exclusiveJobInspector()
    {
        RedissonClient redissonClient = null;
        
        if(GeneralHelper.isStrNotEmpty(redissonClientName))
            redissonClient = SpringContextHolder.getBean(redissonClientName, RedissonClient.class);
        else
            redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        
        return new ExclusiveJobInspector(redissonClient);
    }
}
