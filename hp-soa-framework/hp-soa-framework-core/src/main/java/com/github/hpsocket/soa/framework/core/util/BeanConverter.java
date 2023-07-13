package com.github.hpsocket.soa.framework.core.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.core.Converter;

public class BeanConverter
{
	private static final Converter PROPS_CONVERTER = new PropertiesConverter();
	private static final Converter PROPS_DEEP_CONVERTER = new PropertiesDeepConverter();
	private static final ConcurrentHashMap<Pair<Class<?>, Class<?>>, BeanCopier> COPIERS = new ConcurrentHashMap<>();
	
	public static final <S, D> D fastCopy(S src, D dest)
	{
		return doCopy(src, dest, PROPS_CONVERTER);
	}
	
	public static final <S, D> D fastDeepCopy(S src, D dest)
	{
		return doCopy(src, dest, PROPS_DEEP_CONVERTER);
	}
	
	private static final <S, D> D doCopy(S src, D dest, Converter converter)
	{
		Class<?> clsSrc	 = src.getClass();
		Class<?> clsDest = dest.getClass();
		
		BeanCopier copier = getBeanCopier(clsSrc, clsDest);
		
		copier.copy(src, dest, converter);
		
		return dest;
	}
	
	public static final <S, D> D fastCreate(S src, Class<D> clazz)
	{
		return doCreate(src, clazz, PROPS_CONVERTER);
	}

	public static final <S, D> D fastDeepCreate(S src, Class<D> clazz)
	{
		return doCreate(src, clazz, PROPS_DEEP_CONVERTER);
	}

	private static final <S, D> D doCreate(S src, Class<D> clazz, Converter converter)
	{
		if(src == null)
			return null;
		
		D dest = createInstance(clazz);
		
		return doCopy(src, dest, converter);
	}
	
	private static class PropertiesConverter implements Converter
	{
		@Override
		@SuppressWarnings({ "rawtypes" })
		public Object convert(Object value, Class clsDest, Object context)
		{
			if(value == null)
				return null;
			
			return tryCopy(value, clsDest);
		}

	}

	private static class PropertiesDeepConverter implements Converter
	{
		@Override
		@SuppressWarnings({ "rawtypes" })
		public Object convert(Object value, Class clsDest, Object context)
		{
			if(value == null)
				return null;
			
			return tryDeepCopy(value, clsDest);
		}
	}

	private static BeanCopier getBeanCopier(Class<?> clsSrc, Class<?> clsDest)
	{
		Pair<Class<?>, Class<?>> key = new Pair<Class<?>, Class<?>>(clsSrc, clsDest);
		
		BeanCopier copier = COPIERS.get(key);
		
		if(copier == null)
		{
			synchronized(COPIERS)
			{
				copier = COPIERS.get(key);
				
				if(copier == null)
				{
					copier = BeanCopier.create(clsSrc, clsDest, true);
					COPIERS.putIfAbsent(key, copier);
				}
			}
		}
		
		return copier;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Object tryCopy(Object value, Class clsDest)
	{
		Class<?> clsSrc = value.getClass();
		
		if(	clsDest.equals(clsSrc)
		||	clsDest.isAssignableFrom(clsSrc)
		||	BeanHelper.isSimpleType(clsSrc)
		|| 	Collection.class.isAssignableFrom(clsSrc)
		|| 	Map.class.isAssignableFrom(clsSrc)
		|| 	clsSrc.isArray()
		)
			return value;
		
		return fastCreate(value, clsDest);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Object tryDeepCopy(Object value, Class clsDest)
	{
		Object dest = null;

		if(BeanHelper.isSimpleType(clsDest))
			dest = value;
		else if(Collection.class.isAssignableFrom(clsDest))
		{
			dest = BeanHelper.getRealCollectionClass(clsDest);
			
			((Collection<Object>)dest).addAll((Collection<Object>)value);
		}
		else if(Map.class.isAssignableFrom(clsDest))
		{
			dest = BeanHelper.getRealMapClass(clsDest);
			
			((Map<Object, Object>)dest).putAll((Map<Object, Object>)value);
		}
		else if(clsDest.isArray())
		{
			int length = Array.getLength(value);
			dest = Array.newInstance(value.getClass().getComponentType(), length);
			
			System.arraycopy(value, 0, dest, 0, length);
		}
		else
		{
			dest = fastDeepCreate(value, clsDest);
		}
		
		return dest;
	}
	
	private static <T> T createInstance(Class<T> clazz)
	{
		try
		{
			return clazz.getDeclaredConstructor().newInstance();
		}
		catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static final <S, D> D beanMapCopy(S src, D dest)
	{
		return beanMapCopy(src, dest, false, false, false);
	}
	
	public static final <S, D> D beanMapCopy(S src, D dest, boolean deep)
	{
		return beanMapCopy(src, dest, deep, false, false);
	}
	
	public static final <S, D> D beanMapCopy(S src, D dest, boolean deep, boolean ignoreSrcNull, boolean ignoreDestNotNull)
	{
		BeanMap srcMap	= BeanMap.create(src);
		BeanMap destMap	= BeanMap.create(dest);
		
		for(Object key : srcMap.keySet())
		{
			Object srcVal = srcMap.get(key);
			
			if(ignoreSrcNull && srcVal == null)
				continue;
			
			Class<?> clsDest = destMap.getPropertyType(key.toString());
			
			if(clsDest == null)
				continue;

			Object destVal	 = destMap.get(key);
			boolean keepDest = (ignoreDestNotNull && destVal != null);
			
			if(srcVal == null)
			{
				if(!keepDest)
				{
					if(BeanHelper.isNativeType(clsDest))
						destMap.put(key, clsDest != boolean.class ? Byte.valueOf((byte)0) : Boolean.FALSE);
					else
						destMap.put(key, srcVal);
				}
				
				continue;
			}
			
			if(BeanHelper.isSimpleType(clsDest))
			{
				if(!keepDest)
					destMap.put(key, srcVal);
				
				continue;
			}
			
			boolean sepcialClass = (Collection.class.isAssignableFrom(clsDest)
									|| Map.class.isAssignableFrom(clsDest)
									|| clsDest.isArray());
			
			if(keepDest && sepcialClass)
				continue;
			
			if(deep)
			{
				if(destVal == null || sepcialClass)
					destVal = tryDeepCopy(srcVal, clsDest);
				else
					beanMapCopy(srcVal, destVal, deep, ignoreSrcNull, ignoreDestNotNull);
				
				destMap.put(key, destVal);
			}
			else
			{
				if(clsDest.isAssignableFrom(srcVal.getClass()))
					destMap.put(key, srcVal);
				else
					destMap.put(key, tryCopy(srcVal, clsDest));
			}
		}
		
		return dest;
	}

	public static final <S, D> D beanMapCreate(S src, Class<D> clazz)
	{
		return beanMapCreate(src, clazz, false, false, false);
	}
	
	public static final <S, D> D beanMapCreate(S src, Class<D> clazz, boolean deep)
	{
		return beanMapCreate(src, clazz, deep, false, false);
	}
	
	public static final <S, D> D beanMapCreate(S src, Class<D> clazz, boolean deep, boolean ignoreSrcNull, boolean ignoreDestNotNull)
	{
		if(src == null)
			return null;
		
		D dest = createInstance(clazz);
		
		return beanMapCopy(src, dest, deep, ignoreSrcNull, ignoreDestNotNull);
	}
	
}
