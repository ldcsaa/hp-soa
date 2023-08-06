
package com.github.hpsocket.soa.starter.data.redis.serializer;

import java.time.Duration;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.esotericsoftware.kryo.Kryo;

public class KryoPool extends GenericObjectPool<Kryo>
{
	public KryoPool()
	{
		super(new KryoFactory());
		
		this.setMaxIdle(500);
		this.setMinIdle(50);
		this.setMaxTotal(50000);
		this.setMaxWait(Duration.ofMillis(10));
	}

	public static class KryoFactory extends BasePooledObjectFactory<Kryo>
	{
		@Override
		public PooledObject<Kryo> wrap(Kryo kryo)
		{
			return new DefaultPooledObject<Kryo>(kryo);
		}

		@Override
		public Kryo create() throws Exception
		{
			Kryo kryo = new Kryo();
			kryo.setRegistrationRequired(false);
			return kryo;
		}
	}

}
