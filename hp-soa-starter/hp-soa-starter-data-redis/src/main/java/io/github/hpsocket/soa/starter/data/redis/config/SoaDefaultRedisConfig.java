
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
    public static final String redissonAutoConfigurationCustomizerBeanName = "redissonAutoConfigurationCustomizer";
    public static final String soaDefaultRedissionPropertiesBeanName = "soaDefaultRedissionProperties";
    public static final String soaDefaultRedisPropertiesBeanName = "soaDefaultRedisProperties";
    public static final String redisStringKeyGeneratorBeanName = "redisStringKeyGenerator";
    public static final String redisTemplateBeanName = "redisTemplate";
    public static final String redisStringTemplateBeanName = "redisStringTemplate";
    public static final String redisJsonTemplateBeanName = "redisJsonTemplate";
    public static final String redisGenericJsonTemplateBeanName = "redisGenericJsonTemplate";
    public static final String redisKryoTemplateBeanName = "redisKryoTemplate";
    public static final String redisKryoNotNullTemplateBeanName = "redisKryoNotNullTemplate";
    public static final String redisReactiveTemplateBeanName = "redisReactiveTemplate";
    public static final String redisReactiveStringTemplateBeanName = "redisReactiveStringTemplate";
    public static final String redisCacheManagerBeanName = "redisCacheManager";
    public static final String redisDefaultCacheConfigurationBeanName = "redisDefaultCacheConfiguration";
    public static final String redisInitialCacheConfigurationsBeanName = "redisInitialCacheConfigurations";
    public static final String redisConnectionFactoryBeanName = "redisConnectionFactory";
    public static final String redissonReactiveClientBeanName = "redissonReactiveClient";
    public static final String redissonRxClientBeanName = "redissonRxClient";
    public static final String redissonClientBeanName = "redissonClient";

    public SoaDefaultRedisConfig(
        @Qualifier(redissonAutoConfigurationCustomizerBeanName) ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        @Qualifier(soaDefaultRedissionPropertiesBeanName) ObjectProvider<RedissonProperties> SoaRedissionProperties,
        @Qualifier(soaDefaultRedisPropertiesBeanName) ObjectProvider<RedisProperties> SoaRedisProperties)
    {
        super(redissonAutoConfigurationCustomizers, SoaRedissionProperties, SoaRedisProperties);
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
    public <T> RedisTemplate<String, T> redisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
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

    /** 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisJsonTemplateBeanName)
    public RedisTemplate<String, Object> jsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.jsonRedisTemplate(redisConnectionFactory);
    }

    /** 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
    @Override
    @Bean(redisGenericJsonTemplateBeanName)
    @ConditionalOnMissingBean(name = redisGenericJsonTemplateBeanName)
    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) RedisConnectionFactory redisConnectionFactory)
    {
        return super.genericJsonRedisTemplate(redisConnectionFactory);
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
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(@Qualifier(redisConnectionFactoryBeanName) ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
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
        @Qualifier(redisDefaultCacheConfigurationBeanName) RedisCacheConfiguration redisDefaultCacheConfiguration,
        @Qualifier(redisInitialCacheConfigurationsBeanName) MapPropertySource redisInitialCacheConfigurations)
    {
        return super.redisCacheManager(redisConnectionFactory, redisDefaultCacheConfiguration, redisInitialCacheConfigurations);
    }
    
    /** 默认 {@linkplain RedisCacheConfiguration} */
    @Override
    @Bean(redisDefaultCacheConfigurationBeanName)
    @ConditionalOnMissingBean(name = redisDefaultCacheConfigurationBeanName)
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return super.redisDefaultCacheConfiguration();
    }
    
    /** 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
    @Override
    @Bean(redisInitialCacheConfigurationsBeanName)
    @ConditionalOnMissingBean(name = redisInitialCacheConfigurationsBeanName)
    public MapPropertySource redisInitialCacheConfigurations()
    {
        return super.redisInitialCacheConfigurations();
    }
    
    @Override
    @Primary
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
