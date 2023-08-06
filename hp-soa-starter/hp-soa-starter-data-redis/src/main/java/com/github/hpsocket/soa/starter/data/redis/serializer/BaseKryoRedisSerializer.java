
package com.github.hpsocket.soa.starter.data.redis.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public abstract class BaseKryoRedisSerializer<T> implements RedisSerializer<T>
{
	private static final KryoPool kryoPool = new KryoPool();
	
	protected abstract boolean checkDdeserializeContent(byte[] bytes);

	@Override
	public byte[] serialize(T t) throws SerializationException
	{
		Kryo kryo = null;
		
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream(); Output output = new Output(bos);)
		{
			kryo = kryoPool.borrowObject();
			kryo.writeClassAndObject(output, t);
			
			return bos.toByteArray();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(kryo != null)
			{
				kryoPool.returnObject(kryo);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) throws SerializationException
	{
		Kryo kryo = null;
		
		if(!checkDdeserializeContent(bytes))
		{
			return null;
		}

		try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes); Input input = new Input(bis);)
		{
			kryo = kryoPool.borrowObject();
			return (T)kryo.readClassAndObject(input);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(kryo != null)
			{
				kryoPool.returnObject(kryo);
			}
		}
	}
}
