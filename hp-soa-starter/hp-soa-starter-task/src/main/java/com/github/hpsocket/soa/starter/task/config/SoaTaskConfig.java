
package com.github.hpsocket.soa.starter.task.config;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.core.thread.SynchronousRejectedExecutionHandler;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.support.WebServerHelper;
import com.github.hpsocket.soa.starter.task.properties.SoaTaskProperties;
import com.github.hpsocket.soa.starter.task.properties.SoaTaskProperties.Execution;

@EnableAsync
@EnableScheduling
@AutoConfiguration
@EnableConfigurationProperties({SoaTaskProperties.class})
@ConditionalOnProperty(name = "spring.task.enabled", matchIfMissing = true)
public class SoaTaskConfig
{
	private SoaTaskProperties soaTaskProperties;

	public SoaTaskConfig(SoaTaskProperties soaTaskProperties)
	{
		this.soaTaskProperties = soaTaskProperties;
	}
	
	@Bean("taskExecutor")
	AsyncTaskExecutor taskExecutor()
	{
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		Execution execution = soaTaskProperties.getExecution();
		
		executor.setThreadNamePrefix(execution.getThreadNamePrefix());
		executor.setCorePoolSize(execution.getPool().getCoreSize());
		executor.setMaxPoolSize(execution.getPool().getMaxSize());
		executor.setQueueCapacity(execution.getPool().getQueueCapacity());
		executor.setKeepAliveSeconds(execution.getPool().getKeepAlive());
		executor.setAllowCoreThreadTimeOut(execution.getPool().isAllowCoreThreadTimeout());
		executor.setWaitForTasksToCompleteOnShutdown(execution.getShutdown().isAwaitTermination());
		executor.setAwaitTerminationSeconds(execution.getShutdown().getAwaitTerminationPeriod());
		
		String rejectionPolicy = execution.getPool().getRejectionPolicy();
		RejectedExecutionHandler rjh = null;
		
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
		
		executor.setRejectedExecutionHandler(rjh);
		
		executor.setTaskDecorator(new TaskDecorator()
		{	
			@Override
			public Runnable decorate(Runnable runnable)
			{
				return new Runnable()
				{
					@Override
					public void run()
					{
						MdcAttr mdcAttr = WebServerHelper.createMdcAddr(true);
						
						try
						{
							mdcAttr.putMdc();
							runnable.run();
						}
						finally
						{
							mdcAttr.removeMdc();
						}
					}
	
				};
			}
		});

		return executor;
	}
}
