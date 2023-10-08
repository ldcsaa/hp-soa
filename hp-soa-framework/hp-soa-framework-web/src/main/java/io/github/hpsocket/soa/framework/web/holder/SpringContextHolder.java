
package io.github.hpsocket.soa.framework.web.holder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import io.github.hpsocket.soa.framework.web.service.TracingContext;

/** <b>Spring 上下文持有者</b> */
public class SpringContextHolder
{
    private static ApplicationContext applicationContext;
    private static TracingContext tracingContext;

    public SpringContextHolder(ApplicationContext applicationContext)
    {
        SpringContextHolder.applicationContext = applicationContext;
        SpringContextHolder.tracingContext = getBean(TracingContext.class, false);
    }

    public static ApplicationContext getApplicationContext()
    {
        return getApplicationContext(true);
    }

    public static ApplicationContext getApplicationContext(boolean throwExceptionIfNull)
    {
        if(applicationContext == null && throwExceptionIfNull)
            throw new RuntimeException("SpringContextHolder applicationContext has NOT been injected !");

        return applicationContext;
    }

    public static final TracingContext getTracingContext()
    {
        return tracingContext;
    }
    
    public static final void publishEvent(ApplicationEvent event)
    {
        getApplicationContext().publishEvent(event);
    }

    public static final void publishEvent(Object event)
    {
        getApplicationContext().publishEvent(event);
    }

    public static <T> T getBean(String beanId)
    {
        return getBean(getApplicationContext(), beanId, true);
    }

    public static <T> T getBean(String beanId, boolean valid)
    {
        return getBean(getApplicationContext(), beanId, valid);
    }

    public static <T> T getBean(String beanId, Object ... args)
    {
        return getBean(getApplicationContext(), beanId, true, args);
    }

    public static <T> T getBean(String beanId, boolean valid, Object ... args)
    {
        return getBean(getApplicationContext(), beanId, valid, args);
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

    public static <T> T getBean(Class<T> clazz, Object ... args)
    {
        return getBean(getApplicationContext(), clazz, true, args);
    }

    public static <T> T getBean(Class<T> clazz, boolean valid, Object ... args)
    {
        return getBean(getApplicationContext(), clazz, valid, args);
    }

    public static <T> T getBean(ApplicationContext ctx, String beanId)
    {
        return getBean(ctx, beanId, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(ApplicationContext ctx, String beanId, boolean valid)
    {
        T obj = null;

        try
        {
            obj = (T)ctx.getBean(beanId);
        }
        catch(RuntimeException e)
        {
            if(valid)
                throw e;
        }

        return obj;
    }

    public static <T> T getBean(ApplicationContext ctx, String beanId, Object ... args)
    {
        return getBean(ctx, beanId, true, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(ApplicationContext ctx, String beanId, boolean valid, Object ... args)
    {
        T obj = null;

        try
        {
            obj = (T)ctx.getBean(beanId, args);
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

    public static <T> T getBean(ApplicationContext ctx, Class<T> clazz, Object ... args)
    {
        return getBean(ctx, clazz, true, args);
    }

    public static <T> T getBean(ApplicationContext ctx, Class<T> clazz, boolean valid, Object ... args)
    {
        T obj = null;

        try
        {
            obj = ctx.getBean(clazz, args);
        }
        catch(RuntimeException e)
        {
            if(valid)
                throw e;
        }

        return obj;
    }

}
