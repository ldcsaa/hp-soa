
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
    public static final String redissonAutoConfigurationCustomizer = "secondRedissonAutoConfigurationCustomizer";
    public static final String soaDefaultRedissionProperties = "soaSecondRedissionProperties";
    public static final String soaDefaultRedisProperties = "soaSecondRedisProperties";
    public static final String redisStringKeyGenerator = "secondRedisStringKeyGenerator";
    public static final String redisTemplate = "secondRedisTemplate";
    public static final String redisStringTemplate = "secondRedisStringTemplate";
    public static final String redisJsonTemplate = "secondRedisJsonTemplate";
    public static final String redisGenericJsonTemplate = "secondRedisGenericJsonTemplate";
    public static final String redisKryoTemplate = "secondRedisKryoTemplate";
    public static final String redisKryoNotNullTemplate = "secondRedisKryoNotNullTemplate";
    public static final String redisReactiveTemplate = "secondRedisReactiveTemplate";
    public static final String redisReactiveStringTemplate = "secondRedisReactiveStringTemplate";
    public static final String redisCacheManager = "secondRedisCacheManager";
    public static final String redisDefaultCacheConfiguration = "secondRedisDefaultCacheConfiguration";
    public static final String redisInitialCacheConfigurations = "secondRedisInitialCacheConfigurations";
    public static final String redisConnectionFactory = "secondRedisConnectionFactory";
    public static final String redissonReactiveClient = "secondRedissonReactiveClient";
    public static final String redissonRxClient = "secondRedissonRxClient";
    public static final String redissonClient = "secondRedissonClient";

    public SoaSecondRedisConfig(
        @Qualifier(redissonAutoConfigurationCustomizer) ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier(soaDefaultRedissionProperties) ObjectProvider<RedissonProperties> SoaRedissionProperties,
        @Qualifier(soaDefaultRedisProperties) ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissionProperties, SoaRedisProperties);
    }

    /** 第二个 Redis {@linkplain KeyGenerator} */
    @Override
    @Bean(redisStringKeyGenerator)
    @ConditionalOnMissingBean(name = redisStringKeyGenerator)
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 第二个 Redis {@linkplain RedisTemplate} */
    @Override
    @Bean(redisTemplate)
    @ConditionalOnMissingBean(name = redisTemplate)
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis {@linkplain StringRedisTemplate} */
    @Override
    @Bean(redisStringTemplate)
    @ConditionalOnMissingBean(name = redisStringTemplate)
    public StringRedisTemplate stringRedisTemplate(@Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJsonTemplate)
    @ConditionalOnMissingBean(name = redisJsonTemplate)
    public RedisTemplate<String, Object> jsonRedisTemplate(@Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory)
    {
        return super.jsonRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisGenericJsonTemplate)
    @ConditionalOnMissingBean(name = redisGenericJsonTemplate)
    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(@Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJsonRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisKryoTemplate)
    @ConditionalOnMissingBean(name = redisKryoTemplate)
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean(redisKryoNotNullTemplate)
    @ConditionalOnMissingBean(name = redisKryoNotNullTemplate)
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 第二个 {@linkplain ReactiveRedisTemplate} */
    @Override
    @Bean(redisReactiveTemplate)
    @ConditionalOnMissingBean(name = redisReactiveTemplate)
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(@Qualifier(redisConnectionFactory) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第二个 {@linkplain ReactiveStringRedisTemplate} */
    @Override
    @Bean(redisReactiveStringTemplate)
    @ConditionalOnMissingBean(name = redisReactiveStringTemplate)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier(redisConnectionFactory) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第二个 Redis {@linkplain RedisCacheManager} */
    @Override
    @Bean(redisCacheManager)
    @ConditionalOnMissingBean(name = redisCacheManager)
    public RedisCacheManager redisCacheManager(
        @Qualifier(redisConnectionFactory) RedisConnectionFactory redisConnectionFactory,
        @Qualifier(redisDefaultCacheConfiguration) RedisCacheConfiguration redisDefaultCacheConfiguration,
        @Qualifier(redisInitialCacheConfigurations) MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisDefaultCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 第二个 Redis {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean(redisDefaultCacheConfiguration)
    @ConditionalOnMissingBean(name = redisDefaultCacheConfiguration)
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return super.redisDefaultCacheConfiguration();
    }
    
    /** 第二个 Redis 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean(redisInitialCacheConfigurations)
    @ConditionalOnMissingBean(name = redisInitialCacheConfigurations)
    public MapPropertySource redisInitialCacheConfigurations()
    {
        return super.redisInitialCacheConfigurations();
    }
    
    @Override
    @Bean(redisConnectionFactory)
    @ConditionalOnMissingBean(name = redisConnectionFactory)
    public RedissonConnectionFactory redissonConnectionFactory(@Qualifier(redissonClient) RedissonClient redisson)
    {
        return super.redissonConnectionFactory(redisson);
    }

    @Lazy
    @Override
    @Bean(redissonReactiveClient)
    @ConditionalOnMissingBean(name = redissonReactiveClient)
    public RedissonReactiveClient redissonReactive(@Qualifier(redissonClient) RedissonClient redisson)
    {
        return redisson.reactive();
    }

    @Lazy
    @Override
    @Bean(redissonRxClient)
    @ConditionalOnMissingBean(name = redissonRxClient)
    public RedissonRxClient redissonRxJava(@Qualifier(redissonClient) RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @Override
    @Bean(name = redissonClient, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = redissonClient)
    public RedissonClient redisson() throws IOException
    {
        return super.redisson();
    }
}
