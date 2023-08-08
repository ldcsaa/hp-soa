
package com.github.hpsocket.soa.starter.task.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
		AsyncTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		Execution execution = soaTaskProperties.getExecution();
		
		//executor.
		
		return executor;
	}
}
