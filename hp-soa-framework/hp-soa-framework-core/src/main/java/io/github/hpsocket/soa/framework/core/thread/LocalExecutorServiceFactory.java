package io.github.hpsocket.soa.framework.core.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** <b>线程局部线程池工厂</b>：每个线程拥有独立线程池<br><br>
 * （支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）
 */

public class LocalExecutorServiceFactory
{
	public static final LocalExecutorServiceFactory DEFAULT_FACTORY	= new LocalExecutorServiceFactory();
	private static final RejectedExecutionHandler DEFAULT_REJECT_HANDLER = new SynchronousRejectedExecutionHandler();
	
	private ThreadLocal<ThreadPoolExecutor> local = new ThreadLocal<ThreadPoolExecutor>();
	
	public LocalExecutorServiceFactory()
	{
		
	}
	
	public ExecutorService get()
	{
		return get(Integer.MAX_VALUE, 60L, TimeUnit.SECONDS);
	}
	
	public ExecutorService get(int maximumPoolSize)
	{
		return get(maximumPoolSize, 60L, TimeUnit.SECONDS);
	}
	
	public ExecutorService get(long keepAliveTime, TimeUnit unit)
	{
		return get(Integer.MAX_VALUE, keepAliveTime, unit);
	}
	
	public ExecutorService get(int maximumPoolSize, long keepAliveTime, TimeUnit unit)
	{
		return get(maximumPoolSize, keepAliveTime, unit, null);
	}
	
	public ExecutorService get(int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler rejectHandler)
	{
		ThreadPoolExecutor executor = local.get();
		
		if(rejectHandler == null)
			rejectHandler = DEFAULT_REJECT_HANDLER;
		
		if(executor == null)
		{
			executor = createExecutor(0, maximumPoolSize, keepAliveTime, unit, new SynchronousQueue<Runnable>(), rejectHandler);
			local.set(executor);
		}
		else
		{
			if(executor.getMaximumPoolSize() != maximumPoolSize)
				executor.setMaximumPoolSize(maximumPoolSize);
			if(executor.getKeepAliveTime(TimeUnit.NANOSECONDS) != unit.toNanos(keepAliveTime))
				executor.setKeepAliveTime(keepAliveTime, unit);
			if(executor.getRejectedExecutionHandler() != rejectHandler)
				executor.setRejectedExecutionHandler(rejectHandler);
		}
		
		return executor;
	}
	
	public void remove()
	{
		local.remove();
	}
	
	private ThreadPoolExecutor createExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> synchronousQueue, RejectedExecutionHandler rejectHandler)
	{
		return new AsyncThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, synchronousQueue, rejectHandler);
	}

}
