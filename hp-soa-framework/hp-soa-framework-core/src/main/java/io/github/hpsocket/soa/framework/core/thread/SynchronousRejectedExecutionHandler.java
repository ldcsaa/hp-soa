package io.github.hpsocket.soa.framework.core.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

/** <b>阻塞式 {@linkplain RejectedExecutionHandler}</b><br>
 * 当线程池的工作队列已满时，阻塞任务提交并等待。
 */
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
