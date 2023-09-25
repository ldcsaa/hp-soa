package io.github.hpsocket.soa.framework.web.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import io.github.hpsocket.soa.framework.core.thread.AsyncThreadPoolExecutor;
import io.github.hpsocket.soa.framework.web.service.AsyncService;

import lombok.Getter;
import lombok.Setter;

/** <b>异步服务实现</b><br>
 * 实现 {@linkplain AsyncService} 接口，支持调用链跟踪
 */
@Getter
@Setter
public class AsyncServiceImpl implements AsyncService
{
    private AsyncThreadPoolExecutor executor;
    
    public AsyncServiceImpl(AsyncThreadPoolExecutor executor)
    {
        this.executor = executor;
    }
    
    @PreDestroy
    public void preDestroy()
    {
        if(executor != null)
        {
            executor.shutdown();
            executor = null;
        }
    }
    
    @Override
    public void runAsync(Runnable task)
    {
        execute(task);
    }

    @Override
    public void invokeAsync(final Object obj, final Method method, final Object ... args)
    {
        execute(new Runnable() {
            
            @Override
            public void run()
            {
                try
                {
                    method.invoke(obj, args);
                }
                catch(Throwable e)
                {
                    if(e instanceof InvocationTargetException)
                        e = e.getCause();
                    
                    String msg = String.format("invoke async method '%s' exception", method);
                    throw new RuntimeException(msg, e);
                }
            }
        });
        
    }

    @Override
    public void execute(Runnable task)
    {
        executor.execute(task);
    }
    
    @Override
    public Future<?> submit(Runnable task)
    {
        return executor.submit(task);
    }
    
    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        return executor.submit(task);
    }
    
    @Override
    public <T> Future<T> submit(Runnable task, T result)
    {
        return executor.submit(task, result);
    }

}
