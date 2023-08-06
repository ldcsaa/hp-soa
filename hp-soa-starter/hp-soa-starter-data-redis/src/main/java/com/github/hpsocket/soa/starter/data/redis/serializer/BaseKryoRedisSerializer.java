
package com.github.hpsocket.soa.starter.data.redis.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

public abstract class BaseKryoRedisSerializer<T> implements RedisSerializer<T>
{
	private static Pool<Kryo> kryoPool = new Pool<Kryo>(false, true, 3000)
	{
		@Override
		protected Kryo create()
		{
			Kryo kryo = new Kryo();
			
			kryo.setRegistrationRequired(false);
			kryo.setReferences(false);
			
			return kryo;
		}
	};
	
	protected abstract boolean checkDdeserializeContent(byte[] bytes);

	@Override
	public byte[] serialize(T t) throws SerializationException
	{
		Kryo kryo = null;
		
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream(); Output output = new Output(bos);)
		{
			kryo = kryoPool.obtain();
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
				kryoPool.free(kryo);
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
			kryo = kryoPool.obtain();
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
				kryoPool.free(kryo);
			}
		}
	}
}
