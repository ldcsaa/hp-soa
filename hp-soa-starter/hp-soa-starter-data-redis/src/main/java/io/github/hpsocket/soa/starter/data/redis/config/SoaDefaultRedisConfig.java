
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
import org.springframework.context.annotation.Primary;
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

/** <b>默认 Redis 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.redis.host:}' != '' || '${spring.data.redis.url:}' != '' || '${spring.redis.redisson.config:}' != '' || '${spring.redis.redisson.file:}' != ''")
public class SoaDefaultRedisConfig extends SoaAbstractRedisConfig
{
    public static final String redissonAutoConfigurationCustomizerBeanName = "redissonAutoConfigurationCustomizer";
    public static final String soaRedissonPropertiesBeanName = "soaDefaultRedissonProperties";
    public static final String soaRedisPropertiesBeanName = "soaDefaultRedisProperties";
    public static final String redisStringKeyGeneratorBeanName = "redisStringKeyGenerator";
    public static final String redisCacheKeyPrefixBeanName = "redisCacheKeyPrefix";
    public static final String redisTemplateBeanName = "redisTemplate";
    public static final String redisStringTemplateBeanName = "redisStringTemplate";
    public static final String redisNumberTemplateBeanName = "redisNumberTemplate";
    public static final String redisJsonTemplateBeanName = "redisJsonTemplate";
    public static final String redisGenericJsonTemplateBeanName = "redisGenericJsonTemplate";
    public static final String redisJsonBTemplateBeanName = "redisJsonBTemplate";
    public static final String redisGenericJsonBTemplateBeanName = "redisGenericJsonBTemplate";
    public static final String redisJackson2TemplateBeanName = "redisJackson2Template";
    public static final String redisGenericJackson2TemplateBeanName = "redisGenericJackson2Template";
    public static final String redisKryoTemplateBeanName = "redisKryoTemplate";
    public static final String redisKryoNotNullTemplateBeanName = "redisKryoNotNullTemplate";
    public static final String redisReactiveTemplateBeanName = "redisReactiveTemplate";
    public static final String redisReactiveStringTemplateBeanName = "redisReactiveStringTemplate";
    public static final String redisCacheManagerBeanName = "redisCacheManager";
    public static final String redisCacheConfigurationBeanName = "redisCacheConfiguration";
    public static final String redisCustomizeCacheMapBeanName = "redisCustomizeCacheMap";
    public static final String redisInitialCacheConfigurationsBeanName = "redisInitialCacheConfigurations";
    public static final String redisConnectionFactoryBeanName = "redisConnectionFactory";
    public static final String redissonReactiveClientBeanName = "redissonReactiveClient";
    public static final String redissonRxClientBeanName = "redissonRxClient";
    public static final String redissonClientBeanName = "redissonClient";

    public SoaDefaultRedisConfig(
        @Qualifier(redissonAutoConfigurationCustomizerBeanName) ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier(soaRedissonPropertiesBeanName) ObjectProvider<RedissonProperties> SoaRedissonProperties,
        @Qualifier(soaRedisPropertiesBeanName) ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissonProperties, SoaRedisProperties);
    }

    /** 默认 Redis {@linkplain KeyGenerator} */
    @Primary
    @Override
    @Bean(redisStringKeyGeneratorBeanName)
    @ConditionalOnMissingBean(name = redisStringKeyGeneratorBeanName)
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 默认 {@linkplain RedisTemplate} */
    @Primary
    @Override
    @Bean(redisTemplateBeanName)
    @ConditionalOnMissingBean(name = redisTemplateBeanName)
    public RedisTemplate<String, Object> redisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 默认 {@linkplain StringRedisTemplate} */
    @Primary
    @Override
    @Bean(redisStringTemplateBeanName)
    @ConditionalOnMissingBean(name = redisStringTemplateBeanName)
    public StringRedisTemplate stringRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 默认 {@linkplain NumberRedisTemplate} */
    @Primary
    @Override
    @Bean(redisNumberTemplateBeanName)
    @ConditionalOnMissingBean(name = redisNumberTemplateBeanName)
    public NumberRedisTemplate numberRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.numberRedisTemplate(redisConnectionFactory);
    }

    /** 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisJsonTemplateBeanName)
    public RedisTemplate<String, Object> fastJsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.fastJsonRedisTemplate(redisConnectionFactory);
    }

    /** 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
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

    /** 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisKryoTemplateBeanName)
    @ConditionalOnMissingBean(name = redisKryoTemplateBeanName)
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean(redisKryoNotNullTemplateBeanName)
    @ConditionalOnMissingBean(name = redisKryoNotNullTemplateBeanName)
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 默认 {@linkplain ReactiveRedisTemplate} */
    @Primary
    @Override
    @Bean(redisReactiveTemplateBeanName)
    @ConditionalOnMissingBean(name = redisReactiveTemplateBeanName)
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 默认 {@linkplain ReactiveStringRedisTemplate} */
    @Primary
    @Override
    @Bean(redisReactiveStringTemplateBeanName)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 默认 {@linkplain RedisCacheManager} */
    @Primary
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
    
    /** 默认 {@linkplain RedisCacheConfiguration} */
    @Primary
    @Override
    @Bean(redisCacheConfigurationBeanName)
    @ConditionalOnMissingBean(name = redisCacheConfigurationBeanName)
    public RedisCacheConfiguration redisCacheConfiguration(@Qualifier(redisCacheKeyPrefixBeanName) ObjectProvider<CacheKeyPrefix> cacheKeyPrefix)
    {
        return super.redisCacheConfiguration(cacheKeyPrefix);
    }
    
    /** 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Primary
    @Override
    @Bean(redisInitialCacheConfigurationsBeanName)
    @ConditionalOnMissingBean(name = redisInitialCacheConfigurationsBeanName)
    public MapPropertySource redisInitialCacheConfigurations(
        @Qualifier(redisCacheConfigurationBeanName) RedisCacheConfiguration redisCacheConfiguration,
        @Qualifier(redisCustomizeCacheMapBeanName) ObjectProvider<Map<String, Duration>> redisCustomizeCacheMap)
    {
        return super.redisInitialCacheConfigurations(redisCacheConfiguration, redisCustomizeCacheMap);
    }
    
    @Primary
    @Override
    @Bean(redisConnectionFactoryBeanName)
    @ConditionalOnMissingBean(name = redisConnectionFactoryBeanName)
    public RedissonConnectionFactory redissonConnectionFactory(@Qualifier(redissonClientBeanName) RedissonClient redisson)
    {
        return super.redissonConnectionFactory(redisson);
    }

    @Lazy
    @Primary
    @Override
    @Bean(redissonReactiveClientBeanName)
    @ConditionalOnMissingBean(name = redissonReactiveClientBeanName)
    public RedissonReactiveClient redissonReactive(@Qualifier(redissonClientBeanName) RedissonClient redisson)
    {
        return redisson.reactive();
    }

    @Lazy
    @Primary
    @Override
    @Bean(redissonRxClientBeanName)
    @ConditionalOnMissingBean(name = redissonRxClientBeanName)
    public RedissonRxClient redissonRxJava(@Qualifier(redissonClientBeanName) RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @Primary
    @Override
    @Bean(name = redissonClientBeanName, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = redissonClientBeanName)
    public RedissonClient redisson() throws IOException
    {
        return super.redisson();
    }
}
