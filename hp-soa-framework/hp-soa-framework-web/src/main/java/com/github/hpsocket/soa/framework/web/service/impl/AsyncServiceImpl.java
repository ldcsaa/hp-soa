package com.github.hpsocket.soa.framework.web.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.hpsocket.soa.framework.core.thread.MdcThreadPoolExecutor;
import com.github.hpsocket.soa.framework.core.thread.SynchronousRejectedExecutionHandler;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.service.AsyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AsyncServiceImpl implements AsyncService
{
	@Value("${soa.misc.asyncService.tracingSupported:true}")
	private boolean tracingSupported;
	
	private int corePoolSize;
	private int maxPoolSize;
	private int keepAliveSeconds;
	private int queueCapacity;
	private String rejectionPolicy;
	private boolean allowCoreThreadTimeOut;
	
	private MdcThreadPoolExecutor executor;
	
	@PostConstruct
	public void postConstruct()
	{
		if(executor != null)
			return;
		
		RejectedExecutionHandler rjh = null;
		
		BlockingQueue<Runnable> queue = queueCapacity == 0 ? new SynchronousQueue<>() : new LinkedBlockingDeque<>(queueCapacity);
		
		if(GeneralHelper.isStrEmpty(rejectionPolicy))
			rjh = new ThreadPoolExecutor.CallerRunsPolicy();
		else if("CALLER_RUNS".equalsIgnoreCase(rejectionPolicy))
			rjh = new ThreadPoolExecutor.CallerRunsPolicy();
		else if("ABORT".equalsIgnoreCase(rejectionPolicy))
			rjh = new ThreadPoolExecutor.AbortPolicy();
		else if("DISCARD".equalsIgnoreCase(rejectionPolicy))
			rjh = new ThreadPoolExecutor.DiscardPolicy();
		else if("DISCARD_OLDEST".equalsIgnoreCase(rejectionPolicy))
			rjh = new ThreadPoolExecutor.DiscardOldestPolicy();
		else if("SYNC".equalsIgnoreCase(rejectionPolicy))
			rjh = new SynchronousRejectedExecutionHandler();
		else
			throw new RuntimeException(String.format("invalid rejectionPolicy '%s'", rejectionPolicy));
		
		executor = new MdcThreadPoolExecutor(	corePoolSize,
												maxPoolSize,
												keepAliveSeconds,
												TimeUnit.SECONDS,
												queue,
												rjh);
		
		executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
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
