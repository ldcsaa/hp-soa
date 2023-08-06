
package com.github.hpsocket.soa.starter.data.redis.config;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.github.hpsocket.soa.starter.data.redis.serializer.KryoNotNullRedisSerializer;
import com.github.hpsocket.soa.starter.data.redis.serializer.KryoRedisSerializer;

@AutoConfiguration
@EnableConfigurationProperties({})
@ConditionalOnBean(RedissonAutoConfiguration.class)
public class SoaRedisConfig
{
	private static final KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<Object>();
	private static final KryoNotNullRedisSerializer<Object> kryoNotNullRedisSerializer = new KryoNotNullRedisSerializer<Object>();

	@Bean("kryoRedisTemplate")
	public RedisTemplate<Object, Object> kryoRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		
		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(kryoRedisSerializer);
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(kryoRedisSerializer);
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}

	@Bean("kryoNotNullRedisTemplate")
	public RedisTemplate<Object, Object> kryoNotNullRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		
		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(kryoNotNullRedisSerializer);
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(kryoNotNullRedisSerializer);
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}

}
