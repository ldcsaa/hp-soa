
package io.github.hpsocket.soa.starter.data.redis.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonAutoConfigurationCustomizer;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;

/** <b>默认 Redis 实例之外第二个 Redis 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.redis-second.host:}' != '' || '${spring.data.redis-second.url:}' != '' || '${spring.redis.redisson-second.config:}' != '' || '${spring.redis.redisson-second.file:}' != ''")
public class SoaSecondRedisConfig extends SoaAbstractRedisConfig
{
    public SoaSecondRedisConfig(
        @Qualifier("secondRedissonAutoConfigurationCustomizer") ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier("soaSecondRedissionProperties") ObjectProvider<RedissonProperties> SoaRedissionProperties,
        @Qualifier("soaSecondRedisProperties") ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissionProperties, SoaRedisProperties);
    }

    /** 第二个 Redis {@linkplain KeyGenerator} */
    @Override
    @Bean("secondRedisStringKeyGenerator")
    @ConditionalOnMissingBean(name = "secondRedisStringKeyGenerator")
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 第二个 Redis {@linkplain RedisTemplate} */
    @Override
    @Bean("secondRedisTemplate")
    @ConditionalOnMissingBean(name = "secondRedisTemplate")
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis {@linkplain StringRedisTemplate} */
    @Override
    @Bean("secondRedisStringTemplate")
    @ConditionalOnMissingBean(name = "secondRedisStringTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("secondRedisJsonTemplate")
    @ConditionalOnMissingBean(name = "secondRedisJsonTemplate")
    public RedisTemplate<String, Object> jsonRedisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.jsonRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("secondRedisGenericJsonTemplate")
    @ConditionalOnMissingBean(name = "secondRedisGenericJsonTemplate")
    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJsonRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("secondRedisKryoTemplate")
    @ConditionalOnMissingBean(name = "secondRedisKryoTemplate")
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean("secondRedisKryoNotNullTemplate")
    @ConditionalOnMissingBean(name = "secondRedisKryoNotNullTemplate")
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 第二个 {@linkplain ReactiveRedisTemplate} */
    @Override
    @Bean("secondRedisReactiveTemplate")
    @ConditionalOnMissingBean(name = "secondRedisReactiveTemplate")
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(@Qualifier("secondRedisConnectionFactory") ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第二个 {@linkplain ReactiveStringRedisTemplate} */
    @Override
    @Bean("secondRedisReactiveStringTemplate")
    @ConditionalOnMissingBean(name = "secondRedisReactiveStringTemplate")
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier("secondRedisConnectionFactory") ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第二个 Redis {@linkplain RedisCacheManager} */
    @Override
    @Bean("secondRedisCacheManager")
    @ConditionalOnMissingBean(name = "secondRedisCacheManager")
    public RedisCacheManager redisCacheManager(
        @Qualifier("secondRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
        @Qualifier("secondRedisDefaultCacheConfiguration") RedisCacheConfiguration redisDefaultCacheConfiguration,
        @Qualifier("secondRedisInitialCacheConfigurations") MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisDefaultCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 第二个 Redis {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean("secondRedisDefaultCacheConfiguration")
    @ConditionalOnMissingBean(name = "secondRedisDefaultCacheConfiguration")
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return super.redisDefaultCacheConfiguration();
    }
    
    /** 第二个 Redis 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean("secondRedisInitialCacheConfigurations")
    @ConditionalOnMissingBean(name = "secondRedisInitialCacheConfigurations")
    public MapPropertySource redisInitialCacheConfigurations()
    {
        return super.redisInitialCacheConfigurations();
    }
    
    @Override
    @Bean("secondRedisConnectionFactory")
    @ConditionalOnMissingBean(name = "secondRedisConnectionFactory")
    public RedissonConnectionFactory redissonConnectionFactory(@Qualifier("secondRedissonClient") RedissonClient redisson)
    {
        return super.redissonConnectionFactory(redisson);
    }

    @Lazy
    @Override
    @Bean("secondRedissonReactiveClient")
    @ConditionalOnMissingBean(name = "secondRedissonReactiveClient")
    public RedissonReactiveClient redissonReactive(@Qualifier("secondRedissonClient") RedissonClient redisson)
    {
        return redisson.reactive();
    }

    @Lazy
    @Override
    @Bean("secondRedissonRxClient")
    @ConditionalOnMissingBean(name = "secondRedissonRxClient")
    public RedissonRxClient redissonRxJava(@Qualifier("secondRedissonClient") RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @Override
    @Bean(name = "secondRedissonClient", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "secondRedissonClient")
    public RedissonClient redisson() throws IOException
    {
        return super.redisson();
    }
}
