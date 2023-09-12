
package io.github.hpsocket.soa.starter.data.redis.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** <b>Kryo 序列化器基类</b> */
public abstract class BaseKryoRedisSerializer<T> implements RedisSerializer<T>
{
	private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(BaseKryoRedisSerializer::createKryo);
	
	/*
	private static Pool<Kryo> kryoPool = new Pool<Kryo>(true, true, 1000)
	{
		@Override
		protected Kryo create()
		{
			return createKryo();
		}
	};
	*/
	
	private static Kryo createKryo()
	{
		Kryo kryo = new Kryo();
		
		kryo.setRegistrationRequired(false);
		kryo.setReferences(false);
		
		return kryo;
	}
	
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
			kryo = kryos.get();
			kryo.writeClassAndObject(output, t);

			return output.getBuffer();
		}
		catch(Exception e)
		{
			throw new SerializationException(e.getMessage(), e);
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
			kryo = kryos.get();
			return (T)kryo.readClassAndObject(input);
		}
		catch(Exception e)
		{
			throw new SerializationException(e.getMessage(), e);
		}
	}
}
