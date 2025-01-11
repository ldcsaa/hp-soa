package io.github.hpsocket.soa.starter.data.redis.serializer;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class NumberRedisSerializer implements RedisSerializer<Number>
{
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public byte[] serialize(Number value) throws SerializationException
    {
        return (value == null ? null : value.toString().getBytes(CHARSET));
    }

    @Override
    public Number deserialize(byte[] bytes) throws SerializationException
    {
        return (bytes == null ? null : new BigDecimal(new String(bytes, CHARSET)));
    }

}
