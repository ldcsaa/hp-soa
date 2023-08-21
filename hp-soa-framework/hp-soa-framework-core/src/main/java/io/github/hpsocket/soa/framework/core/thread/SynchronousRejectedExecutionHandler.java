package io.github.hpsocket.soa.framework.core.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronousRejectedExecutionHandler implements RejectedExecutionHandler
{
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
	{
		if(!executor.isShutdown())
		{
			try
			{
				executor.getQueue().put(r);
			}
			catch(InterruptedException e)
			{
				log.error("submit task {} interrupted", r, e);
			}
		}
	}

}
