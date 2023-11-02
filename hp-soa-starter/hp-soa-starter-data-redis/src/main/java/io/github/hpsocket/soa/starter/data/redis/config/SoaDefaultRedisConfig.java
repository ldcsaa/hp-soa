
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
import org.springframework.context.annotation.Primary;
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

/** <b>默认 Redis 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.redis.host:}' != '' || '${spring.data.redis.url:}' != '' || '${spring.redis.redisson.config:}' != '' || '${spring.redis.redisson.file:}' != ''")
public class SoaDefaultRedisConfig extends SoaAbstractRedisConfig
{
    public SoaDefaultRedisConfig(
        @Qualifier("redissonAutoConfigurationCustomizer") ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier("soaDefaultRedissionProperties") ObjectProvider<RedissonProperties> SoaRedissionProperties,
        @Qualifier("soaDefaultRedisProperties") ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissionProperties, SoaRedisProperties);
    }

    /** 默认 Redis {@linkplain KeyGenerator} */
    @Primary
    @Override
    @Bean("redisStringKeyGenerator")
    @ConditionalOnMissingBean(name = "redisStringKeyGenerator")
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 默认 {@linkplain RedisTemplate} */
    @Primary
    @Override
    @Bean("redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 默认 {@linkplain StringRedisTemplate} */
    @Primary
    @Override
    @Bean("redisStringTemplate")
    @ConditionalOnMissingBean(name = "redisStringTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("redisJsonTemplate")
    @ConditionalOnMissingBean(name = "redisJsonTemplate")
    public RedisTemplate<String, Object> jsonRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.jsonRedisTemplate(redisConnectionFactory);
    }

    /** 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("redisGenericJsonTemplate")
    @ConditionalOnMissingBean(name = "redisGenericJsonTemplate")
    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJsonRedisTemplate(redisConnectionFactory);
    }

    /** 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean("redisKryoTemplate")
    @ConditionalOnMissingBean(name = "redisKryoTemplate")
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean("redisKryoNotNullTemplate")
    @ConditionalOnMissingBean(name = "redisKryoNotNullTemplate")
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 默认 {@linkplain ReactiveRedisTemplate} */
    @Primary
    @Override
    @Bean("redisReactiveTemplate")
    @ConditionalOnMissingBean(name = "redisReactiveTemplate")
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(@Qualifier("redisConnectionFactory") ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 默认 {@linkplain ReactiveStringRedisTemplate} */
    @Primary
    @Override
    @Bean("redisReactiveStringTemplate")
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier("redisConnectionFactory") ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 默认 {@linkplain RedisCacheManager} */
    @Primary
    @Override
    @Bean("redisCacheManager")
    @ConditionalOnMissingBean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(
        @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
        @Qualifier("redisDefaultCacheConfiguration") RedisCacheConfiguration redisDefaultCacheConfiguration,
        @Qualifier("redisInitialCacheConfigurations") MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisDefaultCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 默认 {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean("redisDefaultCacheConfiguration")
    @ConditionalOnMissingBean(name = "redisDefaultCacheConfiguration")
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return super.redisDefaultCacheConfiguration();
    }
    
    /** 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean("redisInitialCacheConfigurations")
    @ConditionalOnMissingBean(name = "redisInitialCacheConfigurations")
    public MapPropertySource redisInitialCacheConfigurations()
    {
        return super.redisInitialCacheConfigurations();
    }
    
    @Override
    @Primary
    @Bean("redisConnectionFactory")
    @ConditionalOnMissingBean(name = "redisConnectionFactory")
    public RedissonConnectionFactory redissonConnectionFactory(@Qualifier("redissonClient") RedissonClient redisson)
    {
        return super.redissonConnectionFactory(redisson);
    }

    @Lazy
    @Primary
    @Override
    @Bean("redissonReactiveClient")
    @ConditionalOnMissingBean(name = "redissonReactiveClient")
    public RedissonReactiveClient redissonReactive(@Qualifier("redissonClient") RedissonClient redisson)
    {
        return redisson.reactive();
    }

    @Lazy
    @Primary
    @Override
    @Bean("redissonRxClient")
    @ConditionalOnMissingBean(name = "redissonRxClient")
    public RedissonRxClient redissonRxJava(@Qualifier("redissonClient") RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @Primary
    @Override
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "redissonClient")
    public RedissonClient redisson() throws IOException
    {
        return super.redisson();
    }
}
