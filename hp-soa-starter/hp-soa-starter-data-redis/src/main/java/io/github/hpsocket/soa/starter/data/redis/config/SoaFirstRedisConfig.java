
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

/** <b>默认 Redis 实例之外第一个 Redis 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.redis-first.host:}' != '' || '${spring.data.redis-first.url:}' != '' || '${spring.redis.redisson-first.config:}' != '' || '${spring.redis.redisson-first.file:}' != ''")
public class SoaFirstRedisConfig extends SoaAbstractRedisConfig
{
    public SoaFirstRedisConfig(
        @Qualifier("firstRedissonAutoConfigurationCustomizer") ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier("soaFirstRedissionProperties") ObjectProvider<RedissonProperties> SoaRedissionProperties,
        @Qualifier("soaFirstRedisProperties") ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissionProperties, SoaRedisProperties);
    }

    /** 第一个 Redis {@linkplain KeyGenerator} */
    @Override
    @Bean("firstRedisStringKeyGenerator")
    @ConditionalOnMissingBean(name = "firstRedisStringKeyGenerator")
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 第一个 Redis {@linkplain RedisTemplate} */
    @Override
    @Bean("firstRedisTemplate")
    @ConditionalOnMissingBean(name = "firstRedisTemplate")
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis {@linkplain StringRedisTemplate} */
    @Override
    @Bean("firstRedisStringTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("firstRedisJsonTemplate")
    @ConditionalOnMissingBean(name = "firstRedisJsonTemplate")
    public RedisTemplate<String, Object> jsonRedisTemplate(@Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.jsonRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("firstRedisGenericJsonTemplate")
    @ConditionalOnMissingBean(name = "firstRedisGenericJsonTemplate")
    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(@Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJsonRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("firstRedisKryoTemplate")
    @ConditionalOnMissingBean(name = "firstRedisKryoTemplate")
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean("firstRedisKryoNotNullTemplate")
    @ConditionalOnMissingBean(name = "firstRedisKryoNotNullTemplate")
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 第一个 {@linkplain ReactiveRedisTemplate} */
    @Override
    @Bean("firstRedisReactiveTemplate")
    @ConditionalOnMissingBean(name = "firstRedisReactiveTemplate")
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(@Qualifier("firstRedisConnectionFactory") ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第一个 {@linkplain ReactiveStringRedisTemplate} */
    @Override
    @Bean("firstRedisReactiveStringTemplate")
    @ConditionalOnMissingBean(name = "firstRedisReactiveStringTemplate")
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier("firstRedisConnectionFactory") ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第一个 Redis {@linkplain RedisCacheManager} */
    @Override
    @Bean("firstRedisCacheManager")
    @ConditionalOnMissingBean(name = "firstRedisCacheManager")
    public RedisCacheManager redisCacheManager(
        @Qualifier("firstRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
        @Qualifier("firstRedisDefaultCacheConfiguration") RedisCacheConfiguration redisDefaultCacheConfiguration,
        @Qualifier("firstRedisInitialCacheConfigurations") MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisDefaultCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 第一个 Redis {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean("firstRedisDefaultCacheConfiguration")
    @ConditionalOnMissingBean(name = "firstRedisDefaultCacheConfiguration")
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return super.redisDefaultCacheConfiguration();
    }
    
    /** 第一个 Redis 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean("firstRedisInitialCacheConfigurations")
    @ConditionalOnMissingBean(name = "firstRedisInitialCacheConfigurations")
    public MapPropertySource redisInitialCacheConfigurations()
    {
        return super.redisInitialCacheConfigurations();
    }
    
    @Override
    @Bean("firstRedisConnectionFactory")
    @ConditionalOnMissingBean(name = "firstRedisConnectionFactory")
    public RedissonConnectionFactory redissonConnectionFactory(@Qualifier("firstRedissonClient") RedissonClient redisson)
    {
        return super.redissonConnectionFactory(redisson);
    }

    @Lazy
    @Override
    @Bean("firstRedissonReactiveClient")
    @ConditionalOnMissingBean(name = "firstRedissonReactiveClient")
    public RedissonReactiveClient redissonReactive(@Qualifier("firstRedissonClient") RedissonClient redisson)
    {
        return redisson.reactive();
    }

    @Lazy
    @Override
    @Bean("firstRedissonRxClient")
    @ConditionalOnMissingBean(name = "firstRedissonRxClient")
    public RedissonRxClient redissonRxJava(@Qualifier("firstRedissonClient") RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @Override
    @Bean(name = "firstRedissonClient", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "firstRedissonClient")
    public RedissonClient redisson() throws IOException
    {
        return super.redisson();
    }
}
