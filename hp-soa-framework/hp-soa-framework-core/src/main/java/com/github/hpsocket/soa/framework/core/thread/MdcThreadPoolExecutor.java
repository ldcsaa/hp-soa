package com.github.hpsocket.soa.framework.core.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.github.hpsocket.soa.framework.core.mdc.MdcRunnable;

import lombok.Getter;

@Getter
public class MdcThreadPoolExecutor extends ThreadPoolExecutor implements ExecutorService
{
	public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	@Override
	public void execute(Runnable task)
	{
		super.execute(new MdcTask(task));
	}
	
	private static class MdcTask extends MdcRunnable
	{
		private Runnable r;
		
		public MdcTask(Runnable r)
		{
			this.r = r;
		}

		@Override
		public void doRun()
		{
			r.run();
		}
	}

	/*
	@Override
	public <T> Future<T> submit(Callable<T> task)
	{
		return super.submit(new Call<T>(task));
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result)
	{
		return super.submit(new Task(task), result);
	}

	@Override
	public Future<?> submit(Runnable task)
	{
		return super.submit(new Task(task));
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
	{
		List<Callable<T>> ts = new ArrayList<>(tasks.size());
		
		for(Callable<T> t : tasks)
			ts.add(new Call<T>(t));
		
		return super.invokeAll(ts);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException
	{
		List<Callable<T>> ts = new ArrayList<>(tasks.size());
		
		for(Callable<T> t : tasks)
			ts.add(new Call<T>(t));
		
		return super.invokeAll(ts, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
	{
		List<Callable<T>> ts = new ArrayList<>(tasks.size());
		
		for(Callable<T> t : tasks)
			ts.add(new Call<T>(t));
		
		return super.invokeAny(ts);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		List<Callable<T>> ts = new ArrayList<>(tasks.size());
		
		for(Callable<T> t : tasks)
			ts.add(new Call<T>(t));
		
		return super.invokeAny(ts, timeout, unit);
	}
	*/

}
