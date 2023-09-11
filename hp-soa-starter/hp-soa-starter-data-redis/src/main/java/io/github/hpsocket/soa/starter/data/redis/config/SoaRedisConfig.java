
package io.github.hpsocket.soa.starter.data.redis.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.redisson.Redisson;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoNotNullRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoRedisSerializer;

/** <b>HP-SOA Redis 配置</b> */
@AutoConfiguration
@EnableCaching(order = 0)
@AutoConfigureBefore(RedissonAutoConfiguration.class)
@ConditionalOnClass({Redisson.class, RedissonAutoConfiguration.class})
public class SoaRedisConfig
{
	private static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

	private static final FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
	private static final GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer(true);

	private static final KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<Object>();
	private static final KryoNotNullRedisSerializer<Object> kryoNotNullRedisSerializer = new KryoNotNullRedisSerializer<Object>();

	static
	{
		FastJsonConfig config = new FastJsonConfig();
		config.setJSONB(true);
		fastJsonRedisSerializer.setFastJsonConfig(config);
	}

	/** 默认 {@linkplain RedisTemplate} */
	@Primary
	@Bean("redisTemplate")
	public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		return genericJsonRedisTemplate(redisConnectionFactory);
	}

	/** 基于 FastJson 序列化的 {@linkplain RedisTemplate} */
	@Bean("redisJsonTemplate")
	public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(fastJsonRedisSerializer);
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(fastJsonRedisSerializer);
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}

	/** 基于通用 FastJson 序列化的 {@linkplain RedisTemplate} */
	@Bean("redisGenericJsonTemplate")
	public <T> RedisTemplate<String, T> genericJsonRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<String, T> template = new RedisTemplate<String, T>();

		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(genericFastJsonRedisSerializer);
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(genericFastJsonRedisSerializer);
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}

	/** 基于 Kryo 序列化的 {@linkplain RedisTemplate} */
	@Bean("redisKryoTemplate")
	public <T> RedisTemplate<String, T> kryoRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<String, T> template = new RedisTemplate<String, T>();

		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(kryoRedisSerializer);
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(kryoRedisSerializer);
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}

	/** 基于 Kryo 序列化的 {@linkplain RedisTemplate} （不支持存储 null 值）*/
	@Bean("redisKryoNotNullTemplate")
	public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<String, T> template = new RedisTemplate<String, T>();

		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(kryoNotNullRedisSerializer);
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(kryoNotNullRedisSerializer);
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}

	/** 默认 {@linkplain RedisCacheManager} */
	@Bean("redisCacheManager")
	@SuppressWarnings("unchecked")
	@ConditionalOnMissingBean(name = "redisCacheManager")
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
		@Qualifier("rediseDfaultCacheConfiguration") RedisCacheConfiguration rediseDfaultCacheConfiguration,
		@Qualifier("redisInitialCacheConfigurations") MapPropertySource redisInitialCacheConfigurations)
	{
		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(rediseDfaultCacheConfiguration)
			.withInitialCacheConfigurations((Map<String, RedisCacheConfiguration>)(Map<?, ?>)redisInitialCacheConfigurations.getSource())
			.allowCreateOnMissingCache(false);

		return builder.build();

	}
	
	/** 默认 {@linkplain RedisCacheConfiguration} */
	@Bean("rediseDfaultCacheConfiguration")
	@ConditionalOnMissingBean(name = "rediseDfaultCacheConfiguration")
	public RedisCacheConfiguration rediseDfaultCacheConfiguration()
	{
		return createRedisCacheConfiguration(3600);
	}
	
	/** 初始 {@linkplain RedisCacheConfiguration} {@linkplain Map} */
	@SuppressWarnings("unchecked")
	@Bean("redisInitialCacheConfigurations")
	@ConditionalOnMissingBean(name = "redisInitialCacheConfigurations")
	public MapPropertySource redisInitialCacheConfigurations()
	{
		Map<String, RedisCacheConfiguration> cfgs = new LinkedHashMap<>();

		cfgs.put("10s", createRedisCacheConfiguration(10));
		cfgs.put("30s", createRedisCacheConfiguration(30));
		cfgs.put("1m", createRedisCacheConfiguration(60));
		cfgs.put("5m", createRedisCacheConfiguration(300));
		cfgs.put("15m", createRedisCacheConfiguration(900));
		cfgs.put("30m", createRedisCacheConfiguration(1800));
		cfgs.put("1h", createRedisCacheConfiguration(3600));
		cfgs.put("3h", createRedisCacheConfiguration(10800));
		cfgs.put("6h", createRedisCacheConfiguration(21600));
		cfgs.put("12h", createRedisCacheConfiguration(43200));
		cfgs.put("1d", createRedisCacheConfiguration(86400));
		cfgs.put("7d", createRedisCacheConfiguration(604800));
		cfgs.put("15d", createRedisCacheConfiguration(1296000));
		cfgs.put("30d", createRedisCacheConfiguration(2592000));
		
		MapFactoryBean bean = new MapFactoryBean();
		bean.setSourceMap(cfgs);

		return new MapPropertySource("redisInitialCacheConfigurations", (Map<String, Object>)(Map<?, ?>)cfgs);
	}

	private RedisCacheConfiguration createRedisCacheConfiguration(long ttlSeconds)
	{
		return RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(Duration.ofSeconds(ttlSeconds))
			.serializeKeysWith(SerializationPair.fromSerializer(stringRedisSerializer))
			.serializeValuesWith(SerializationPair.fromSerializer(genericFastJsonRedisSerializer));
	}

	/** 默认 Redis {@linkplain KeyGenerator} */
	@Bean("redisStringKeyGenerator")
	@ConditionalOnMissingBean(name = "redisStringKeyGenerator")
	public KeyGenerator keyGenerator()
	{
		return new KeyGenerator()
		{
            @Override
			public Object generate(Object target, Method method, Object... params)
			{
            	StringBuilder sb1 = new StringBuilder()
            						.append(target.getClass().getName())
            						.append(':')
            						.append(method.getName());
				
				String prefix = sb1.toString();
				
				int length = params.length;
				
				if(length == 0)
					return prefix;
				
				StringBuilder sb2 = new StringBuilder();
				
				for(int i = 0; i < length; i++)
				{
					Object obj = params[i];
					sb2.append(obj == null ? "null" : obj.toString());
					
					if(i < length - 1)
						sb2.append(':');
				}
				
				String key = sb2.toString();
				
				if(key.length() > 40)
					key = CryptHelper.md5(key);
				
				return prefix + ':' + key;
			}
		};
	}
}
