package io.github.hpsocket.soa.starter.data.redis.template;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import io.github.hpsocket.soa.starter.data.redis.serializer.NumberRedisSerializer;

public class NumberRedisTemplate extends RedisTemplate<String, Number>
{
    private static final NumberRedisSerializer NUMBER_SERIALIZE = new NumberRedisSerializer();

    public NumberRedisTemplate()
    {
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(NUMBER_SERIALIZE);
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(NUMBER_SERIALIZE);
    }

    public NumberRedisTemplate(RedisConnectionFactory connectionFactory)
    {
        this();
        
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

    @Override
    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection)
    {
        return new DefaultStringRedisConnection(connection);
    }
}
