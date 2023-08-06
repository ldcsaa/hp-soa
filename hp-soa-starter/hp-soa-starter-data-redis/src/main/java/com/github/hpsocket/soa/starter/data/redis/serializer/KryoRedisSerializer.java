
package com.github.hpsocket.soa.starter.data.redis.serializer;

public class KryoRedisSerializer<T> extends BaseKryoRedisSerializer<T>
{
	@Override
	protected boolean checkDdeserializeContent(byte[] bytes)
	{
		if(bytes == null || bytes.length == 0)
		{
			return false;
		}
		
		return true;
	}
}
