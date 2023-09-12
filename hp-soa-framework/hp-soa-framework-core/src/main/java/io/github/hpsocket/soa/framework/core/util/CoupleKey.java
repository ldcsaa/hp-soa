package io.github.hpsocket.soa.framework.core.util;

/** <b>两个元素组成的 {@linkplain java.util.Map Map}/{@linkplain java.util.Set Set} 通用 Key</b> */
public class CoupleKey<K1, K2>
{
	private K1 key1;
	private K2 key2;
	
	public CoupleKey()
	{
	}
	
	public CoupleKey(K1 key1, K2 key2)
	{
		this.key1 = key1;
		this.key2 = key2;
	}
	
	@Override
	public int hashCode()
	{
		int c1 = key1 != null ? key1.hashCode() : 0;
		int c2 = key2 != null ? key2.hashCode() : 0;
		
		return c1 ^ c2;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		
		if(obj instanceof CoupleKey)
		{
			CoupleKey other = (CoupleKey)obj;
			
			if(key1 != null)
			{
				if(!key1.equals(other.key1))
					return false;
			}
			else if(other.key1 != null)
				return false;
			
			if(key2 != null)
				return key2.equals(other.key2);
			else
				return other.key2 == null;
		}
		
		return false;
	}

	public K1 getKey1()
	{
		return key1;
	}

	public void setKey1(K1 key1)
	{
		this.key1 = key1;
	}

	public K2 getKey2()
	{
		return key2;
	}

	public void setKey2(K2 key2)
	{
		this.key2 = key2;
	}
	
}
