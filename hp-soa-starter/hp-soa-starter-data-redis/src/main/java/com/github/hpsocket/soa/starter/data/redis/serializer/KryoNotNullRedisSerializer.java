
package com.github.hpsocket.soa.starter.data.redis.serializer;

public class KryoNotNullRedisSerializer<T> extends BaseKryoRedisSerializer<T>
{
	@Override
	protected boolean checkDdeserializeContent(byte[] bytes)
	{
		if(bytes == null || bytes.length == 0)
		{
			throw new RuntimeException("input bytes is null or length is 0");
		}
		
		return true;
	}
}
