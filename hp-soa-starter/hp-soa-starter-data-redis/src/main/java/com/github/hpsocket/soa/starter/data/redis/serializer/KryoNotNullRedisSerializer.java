
package com.github.hpsocket.soa.starter.data.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;

public class KryoNotNullRedisSerializer<T> extends BaseKryoRedisSerializer<T>
{
	@Override
	protected boolean checkSerializeParam(T t)
	{
		if(t == null)
		{
			throw new SerializationException("input object is null");
		}
		
		return true;
	}

	@Override
	protected boolean checkDeserializeParam(byte[] bytes)
	{
		if(bytes == null || bytes.length == 0)
		{
			throw new SerializationException("input bytes is null or length is 0");
		}
		
		return true;
	}

}
