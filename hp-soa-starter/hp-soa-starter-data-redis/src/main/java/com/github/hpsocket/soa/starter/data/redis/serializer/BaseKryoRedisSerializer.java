
package com.github.hpsocket.soa.starter.data.redis.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

public abstract class BaseKryoRedisSerializer<T> implements RedisSerializer<T>
{
	private static Pool<Kryo> kryoPool = new Pool<Kryo>(true, true, 1000)
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
	
	protected abstract boolean checkSerializeParam(T t);
	protected abstract boolean checkDeserializeParam(byte[] bytes);

	@Override
	public byte[] serialize(T t) throws SerializationException
	{
		if(!checkSerializeParam(t))
		{
			return null;
		}
		
		Kryo kryo = null;
		
		try(Output output = new Output(4096, -1))
		{
			kryo = kryoPool.obtain();
			kryo.writeClassAndObject(output, t);

			return output.getBuffer();
		}
		catch(Exception e)
		{
			throw new SerializationException(e.getMessage(), e);
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
		if(!checkDeserializeParam(bytes))
		{
			return null;
		}

		Kryo kryo = null;
		
		try(Input input = new Input(bytes);)
		{
			kryo = kryoPool.obtain();
			return (T)kryo.readClassAndObject(input);
		}
		catch(Exception e)
		{
			throw new SerializationException(e.getMessage(), e);
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
