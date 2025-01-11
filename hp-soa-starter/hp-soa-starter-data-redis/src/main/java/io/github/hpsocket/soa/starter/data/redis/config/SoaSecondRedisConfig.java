
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
import io.github.hpsocket.soa.starter.data.redis.template.NumberRedisTemplate;

/** <b>默认 Redis 实例之外第二个 Redis 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.redis-second.host:}' != '' || '${spring.data.redis-second.url:}' != '' || '${spring.redis.redisson-second.config:}' != '' || '${spring.redis.redisson-second.file:}' != ''")
public class SoaSecondRedisConfig extends SoaAbstractRedisConfig
{
    public static final String redissonAutoConfigurationCustomizerBeanName = "secondRedissonAutoConfigurationCustomizer";
    public static final String soaDefaultRedissionPropertiesBeanName = "soaSecondRedissionProperties";
    public static final String soaDefaultRedisPropertiesBeanName = "soaSecondRedisProperties";
    public static final String redisStringKeyGeneratorBeanName = "secondRedisStringKeyGenerator";
    public static final String redisTemplateBeanName = "secondRedisTemplate";
    public static final String redisStringTemplateBeanName = "secondRedisStringTemplate";
    public static final String redisNumberTemplateBeanName = "secondRedisNumberTemplate";
    public static final String redisJsonTemplateBeanName = "secondRedisJsonTemplate";
    public static final String redisGenericJsonTemplateBeanName = "secondRedisGenericJsonTemplate";
    public static final String redisKryoTemplateBeanName = "secondRedisKryoTemplate";
    public static final String redisKryoNotNullTemplateBeanName = "secondRedisKryoNotNullTemplate";
    public static final String redisReactiveTemplateBeanName = "secondRedisReactiveTemplate";
    public static final String redisReactiveStringTemplateBeanName = "secondRedisReactiveStringTemplate";
    public static final String redisCacheManagerBeanName = "secondRedisCacheManager";
    public static final String redisDefaultCacheConfigurationBeanName = "secondRedisDefaultCacheConfiguration";
    public static final String redisInitialCacheSourceMapBeanName = "secondRedisInitialCacheSourceMap";
    public static final String redisInitialCacheConfigurationsBeanName = "secondRedisInitialCacheConfigurations";
    public static final String redisConnectionFactoryBeanName = "secondRedisConnectionFactory";
    public static final String redissonReactiveClientBeanName = "secondRedissonReactiveClient";
    public static final String redissonRxClientBeanName = "secondRedissonRxClient";
    public static final String redissonClientBeanName = "secondRedissonClient";

    public SoaSecondRedisConfig(
        @Qualifier(redissonAutoConfigurationCustomizerBeanName) ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier(soaDefaultRedissionPropertiesBeanName) ObjectProvider<RedissonProperties> SoaRedissionProperties,
        @Qualifier(soaDefaultRedisPropertiesBeanName) ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissionProperties, SoaRedisProperties);
    }

    /** 第二个 Redis {@linkplain KeyGenerator} */
    @Override
    @Bean(redisStringKeyGeneratorBeanName)
    @ConditionalOnMissingBean(name = redisStringKeyGeneratorBeanName)
    public KeyGenerator keyGenerator()
    {
        return super.keyGenerator();
    }
                
    /** 第二个 Redis {@linkplain RedisTemplate} */
    @Override
    @Bean(redisTemplateBeanName)
    @ConditionalOnMissingBean(name = redisTemplateBeanName)
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.redisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis {@linkplain StringRedisTemplate} */
    @Override
    @Bean(redisStringTemplateBeanName)
    @ConditionalOnMissingBean(name = redisStringTemplateBeanName)
    public StringRedisTemplate stringRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.stringRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 {@linkplain NumberRedisTemplate} */
    @Override
    @Bean(redisNumberTemplateBeanName)
    @ConditionalOnMissingBean(name = redisNumberTemplateBeanName)
    public NumberRedisTemplate numberRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.numberRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisJsonTemplateBeanName)
    public RedisTemplate<String, Object> jsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.jsonRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisGenericJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisGenericJsonTemplateBeanName)
    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJsonRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisKryoTemplateBeanName)
    @ConditionalOnMissingBean(name = redisKryoTemplateBeanName)
    public <T> RedisTemplate<String, T> kryoRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoRedisTemplate(redisConnectionFactory);
    }

    /** 第二个 Redis 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
    @Override
    @Bean(redisKryoNotNullTemplateBeanName)
    @ConditionalOnMissingBean(name = redisKryoNotNullTemplateBeanName)
    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.kryoNotNullRedisTemplate(redisConnectionFactory);
    }
    
    /** 第二个 {@linkplain ReactiveRedisTemplate} */
    @Override
    @Bean(redisReactiveTemplateBeanName)
    @ConditionalOnMissingBean(name = redisReactiveTemplateBeanName)
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第二个 {@linkplain ReactiveStringRedisTemplate} */
    @Override
    @Bean(redisReactiveStringTemplateBeanName)
    @ConditionalOnMissingBean(name = redisReactiveStringTemplateBeanName)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return super.reactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    /** 第二个 Redis {@linkplain RedisCacheManager} */
    @Override
    @Bean(redisCacheManagerBeanName)
    @ConditionalOnMissingBean(name = redisCacheManagerBeanName)
    public RedisCacheManager redisCacheManager(
        @Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory,
        @Qualifier(redisDefaultCacheConfigurationBeanName) RedisCacheConfiguration redisDefaultCacheConfiguration,
        @Qualifier(redisInitialCacheConfigurationsBeanName) MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisDefaultCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 第二个 Redis {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean(redisDefaultCacheConfigurationBeanName)
    @ConditionalOnMissingBean(name = redisDefaultCacheConfigurationBeanName)
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return super.redisDefaultCacheConfiguration();
    }
    
    @Override
    @Bean(redisInitialCacheSourceMapBeanName)
    @ConditionalOnMissingBean(name = redisInitialCacheSourceMapBeanName)
    public Map<String, RedisCacheConfiguration> redisInitialCacheSourceMap()
    {
        return super.redisInitialCacheSourceMap();
    }
    
    /** 第二个 Redis 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean(redisInitialCacheConfigurationsBeanName)
    @ConditionalOnMissingBean(name = redisInitialCacheConfigurationsBeanName)
    public MapPropertySource redisInitialCacheConfigurations(@Qualifier(redisInitialCacheSourceMapBeanName) Map<String, RedisCacheConfiguration> redisInitialCacheSourceMap)
    {
        return super.redisInitialCacheConfigurations(redisInitialCacheSourceMap);
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
