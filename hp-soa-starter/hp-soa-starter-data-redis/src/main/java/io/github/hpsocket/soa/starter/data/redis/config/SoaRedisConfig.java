package io.github.hpsocket.soa.starter.data.redis.config;

import io.github.hpsocket.soa.starter.data.redis.consumer.aspect.RedisListenerMdcInspector;
import io.github.hpsocket.soa.starter.data.redis.consumer.aspect.RedisListenerTracingInspector;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/** <b>HP-SOA Redis 公共配置</b> */
@AutoConfiguration
@EnableCaching(order = 0)
@ComponentScan("io.github.hpsocket.soa.starter.data.redis.properties")
public class SoaRedisConfig
{
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
}
