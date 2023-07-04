
package org.hpsocket.soa.framework.web.holder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware
{
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
	{
		SpringContextHolder.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext()
	{
		return getApplicationContext(true);
	}

	public static ApplicationContext getApplicationContext(boolean throwExceptionIfNull)
	{
		if(applicationContext == null && throwExceptionIfNull)
			throw new RuntimeException("SpringContextHolder applicationContext have NOT been injected !");

		return applicationContext;
	}

	public static <T> T getService(String serviceName)
	{
		return getBean(getApplicationContext(), serviceName);
	}

	public static <T> T getService(String serviceName, boolean valid)
	{
		return getBean(getApplicationContext(), serviceName, valid);
	}

	public static <T> T getBean(String beanName)
	{
		return getBean(getApplicationContext(), beanName, true);
	}

	public static <T> T getBean(String beanName, boolean valid)
	{
		return getBean(getApplicationContext(), beanName, valid);
	}

	public static <T> T getBean(String beanId, Class<T> clazz)
	{
		return getBean(getApplicationContext(), beanId, clazz, true);
	}

	public static <T> T getBean(String beanId, Class<T> clazz, boolean valid)
	{
		return getBean(getApplicationContext(), beanId, clazz, valid);
	}

	public static <T> T getBean(Class<T> clazz)
	{
		return getBean(getApplicationContext(), clazz, true);
	}

	public static <T> T getBean(Class<T> clazz, boolean valid)
	{
		return getBean(getApplicationContext(), clazz, valid);
	}

	public static <T> T getService(ApplicationContext ctx, String serviceName)
	{
		return getBean(ctx, serviceName);
	}

	public static <T> T getService(ApplicationContext ctx, String serviceName, boolean valid)
	{
		return getBean(ctx, serviceName, valid);
	}

	public static <T> T getBean(ApplicationContext ctx, String beanName)
	{
		return getBean(ctx, beanName, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(ApplicationContext ctx, String beanName, boolean valid)
	{
		T obj = null;

		try
		{
			obj = (T)ctx.getBean(beanName);
		}
		catch(RuntimeException e)
		{
			if(valid)
				throw e;
		}

		return obj;
	}

	public static <T> T getBean(ApplicationContext ctx, String beanId, Class<T> clazz)
	{
		return getBean(ctx, beanId, clazz, true);
	}

	public static <T> T getBean(ApplicationContext ctx, String beanId, Class<T> clazz, boolean valid)
	{
		T obj = null;

		try
		{
			obj = ctx.getBean(beanId, clazz);
		}
		catch(RuntimeException e)
		{
			if(valid)
				throw e;
		}

		return obj;
	}

	public static <T> T getBean(ApplicationContext ctx, Class<T> clazz)
	{
		return getBean(ctx, clazz, true);
	}

	public static <T> T getBean(ApplicationContext ctx, Class<T> clazz, boolean valid)
	{
		T obj = null;

		try
		{
			obj = ctx.getBean(clazz);
		}
		catch(RuntimeException e)
		{
			if(valid)
				throw e;
		}

		return obj;
	}

}
