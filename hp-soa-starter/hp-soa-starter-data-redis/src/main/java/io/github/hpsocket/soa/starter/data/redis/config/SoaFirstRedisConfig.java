
package io.github.hpsocket.soa.starter.data.redis.config;

import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonAutoConfigurationCustomizer;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import io.github.hpsocket.soa.starter.data.redis.template.NumberRedisTemplate;
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
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** <b>默认 Redis 实例之外第一个 Redis 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.redis-first.host:}' != '' || '${spring.data.redis-first.url:}' != '' || '${spring.redis.redisson-first.config:}' != '' || '${spring.redis.redisson-first.file:}' != ''")
public class SoaFirstRedisConfig extends SoaAbstractRedisConfig
{
    public static final String redissonAutoConfigurationCustomizerBeanName = "firstRedissonAutoConfigurationCustomizer";
    public static final String soaRedissonPropertiesBeanName = "soaFirstRedissonProperties";
    public static final String soaRedisPropertiesBeanName = "soaFirstRedisProperties";
    public static final String redisStringKeyGeneratorBeanName = "firstRedisStringKeyGenerator";
    public static final String redisCacheKeyPrefixBeanName = "firstRedisCacheKeyPrefix";
    public static final String redisTemplateBeanName = "firstRedisTemplate";
    public static final String redisStringTemplateBeanName = "firstRedisStringTemplate";
    public static final String redisNumberTemplateBeanName = "firstRedisNumberTemplate";
    public static final String redisJsonTemplateBeanName = "firstRedisJsonTemplate";
    public static final String redisGenericJsonTemplateBeanName = "firstRedisGenericJsonTemplate";
    public static final String redisJsonBTemplateBeanName = "firstRedisJsonBTemplate";
    public static final String redisGenericJsonBTemplateBeanName = "firstRedisGenericJsonBTemplate";
    public static final String redisJackson2TemplateBeanName = "firstRedisJackson2Template";
    public static final String redisGenericJackson2TemplateBeanName = "firstRedisGenericJackson2Template";
    public static final String redisKryoTemplateBeanName = "firstRedisKryoTemplate";
    public static final String redisKryoNotNullTemplateBeanName = "firstRedisKryoNotNullTemplate";
    public static final String redisReactiveTemplateBeanName = "firstRedisReactiveTemplate";
    public static final String redisReactiveStringTemplateBeanName = "firstRedisReactiveStringTemplate";
    public static final String redisCacheManagerBeanName = "firstRedisCacheManager";
    public static final String redisCacheConfigurationBeanName = "firstRedisCacheConfiguration";
    public static final String redisCustomizeCacheMapBeanName = "firstRedisCustomizeCacheMap";
    public static final String redisInitialCacheConfigurationsBeanName = "firstRedisInitialCacheConfigurations";
    public static final String redisConnectionFactoryBeanName = "firstRedisConnectionFactory";
    public static final String redissonReactiveClientBeanName = "firstRedissonReactiveClient";
    public static final String redissonRxClientBeanName = "firstRedissonRxClient";
    public static final String redissonClientBeanName = "firstRedissonClient";

    public SoaFirstRedisConfig(
        @Qualifier(redissonAutoConfigurationCustomizerBeanName) ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier(soaRedissonPropertiesBeanName) ObjectProvider<RedissonProperties> SoaRedissonProperties,
        @Qualifier(soaRedisPropertiesBeanName) ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissonProperties, SoaRedisProperties);
    }

    /** 第一个 Redis {@linkplain KeyGenerator} */
    @Override
    @Bean(redisStringKeyGeneratorBeanName)
    @ConditionalOnMissingBean(name = redisStringKeyGeneratorBeanName)
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 第一个 Redis {@linkplain RedisTemplate} */
    @Override
    @Bean(redisTemplateBeanName)
    @ConditionalOnMissingBean(name = redisTemplateBeanName)
    public RedisTemplate<String, Object> redisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis {@linkplain StringRedisTemplate} */
    @Override
    @Bean(redisStringTemplateBeanName)
    @ConditionalOnMissingBean(name = redisStringTemplateBeanName)
    public StringRedisTemplate stringRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 {@linkplain NumberRedisTemplate} */
    @Override
    @Bean(redisNumberTemplateBeanName)
    @ConditionalOnMissingBean(name = redisNumberTemplateBeanName)
    public NumberRedisTemplate numberRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.numberRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisJsonTemplateBeanName)
    public RedisTemplate<String, Object> fastJsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.fastJsonRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisGenericJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisGenericJsonTemplateBeanName)
    public <T> RedisTemplate<String, T> genericFastJsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericFastJsonRedisTemplate(redisConnectionFactory);
    }

    /** 基于 FastJsonB 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJsonBTemplateBeanName)
    @ConditionalOnMissingBean(name = redisJsonBTemplateBeanName)
    public RedisTemplate<String, Object> fastJsonBRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.fastJsonBRedisTemplate(redisConnectionFactory);
    }

    /** 基于通用 FastJsonB 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisGenericJsonBTemplateBeanName)
    @ConditionalOnMissingBean(name = redisGenericJsonBTemplateBeanName)
    public <T> RedisTemplate<String, T> genericFastJsonBRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericFastJsonBRedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisKryoTemplateBeanName)
    @ConditionalOnMissingBean(name = redisKryoTemplateBeanName)
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 基于 Jackson2 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJackson2TemplateBeanName)
    @ConditionalOnMissingBean(name = redisJackson2TemplateBeanName)
    public RedisTemplate<String, Object> jackson2RedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.jackson2RedisTemplate(redisConnectionFactory);
    }

    /** 基于通用 Jackson2 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisGenericJackson2TemplateBeanName)
    @ConditionalOnMissingBean(name = redisGenericJackson2TemplateBeanName)
    public <T> RedisTemplate<String, T> genericJackson2RedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJackson2RedisTemplate(redisConnectionFactory);
    }

    /** 第一个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean(redisKryoNotNullTemplateBeanName)
    @ConditionalOnMissingBean(name = redisKryoNotNullTemplateBeanName)
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 第一个 {@linkplain ReactiveRedisTemplate} */
    @Override
    @Bean(redisReactiveTemplateBeanName)
    @ConditionalOnMissingBean(name = redisReactiveTemplateBeanName)
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第一个 {@linkplain ReactiveStringRedisTemplate} */
    @Override
    @Bean(redisReactiveStringTemplateBeanName)
    @ConditionalOnMissingBean(name = redisReactiveStringTemplateBeanName)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第一个 Redis {@linkplain RedisCacheManager} */
    @Override
    @Bean(redisCacheManagerBeanName)
    @ConditionalOnMissingBean(name = redisCacheManagerBeanName)
    public RedisCacheManager redisCacheManager(
        @Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory,
        @Qualifier(redisCacheConfigurationBeanName) RedisCacheConfiguration redisCacheConfiguration,
        @Qualifier(redisInitialCacheConfigurationsBeanName) MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 第一个 Redis {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean(redisCacheConfigurationBeanName)
    @ConditionalOnMissingBean(name = redisCacheConfigurationBeanName)
    public RedisCacheConfiguration redisCacheConfiguration(@Qualifier(redisCacheKeyPrefixBeanName) ObjectProvider<CacheKeyPrefix> cacheKeyPrefix)
    {
        return super.redisCacheConfiguration(cacheKeyPrefix);
    }
    
    /** 第一个 Redis 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean(redisInitialCacheConfigurationsBeanName)
    @ConditionalOnMissingBean(name = redisInitialCacheConfigurationsBeanName)
    public MapPropertySource redisInitialCacheConfigurations(
        @Qualifier(redisCacheConfigurationBeanName) RedisCacheConfiguration redisCacheConfiguration,
        @Qualifier(redisCustomizeCacheMapBeanName) ObjectProvider<Map<String, Duration>> redisCustomizeCacheMap)
    {
        return super.redisInitialCacheConfigurations(redisCacheConfiguration, redisCustomizeCacheMap);
    }
    
    @Override
    @Bean(redisConnectionFactoryBeanName)
    @ConditionalOnMissingBean(name = redisConnectionFactoryBeanName)
    public RedissonConnectionFactory redissonConnectionFactory(@Qualifier(redissonClientBeanName) RedissonClient redisson)
    {
        return super.redissonConnectionFactory(redisson);
    }

    @Lazy
    @Override
    @Bean(redissonReactiveClientBeanName)
    @ConditionalOnMissingBean(name = redissonReactiveClientBeanName)
    public RedissonReactiveClient redissonReactive(@Qualifier(redissonClientBeanName) RedissonClient redisson)
    {
        return redisson.reactive();
    }

    @Lazy
    @Override
    @Bean(redissonRxClientBeanName)
    @ConditionalOnMissingBean(name = redissonRxClientBeanName)
    public RedissonRxClient redissonRxJava(@Qualifier(redissonClientBeanName) RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @Override
    @Bean(name = redissonClientBeanName, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = redissonClientBeanName)
    public RedissonClient redisson() throws IOException
    {
        return super.redisson();
    }
}
