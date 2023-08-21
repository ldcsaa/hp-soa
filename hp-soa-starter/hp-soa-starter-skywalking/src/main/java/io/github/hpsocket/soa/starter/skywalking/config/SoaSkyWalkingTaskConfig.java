
package io.github.hpsocket.soa.starter.skywalking.config;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;

import io.github.hpsocket.soa.starter.skywalking.async.TracingRunnableWrapper;
import io.github.hpsocket.soa.starter.task.config.SoaTaskConfig;

@AutoConfiguration
@ConditionalOnClass({TraceContext.class, SoaTaskConfig.class})
@AutoConfigureBefore(SoaTaskConfig.class)
public class SoaSkyWalkingTaskConfig
{
	@Bean("mdcTaskDecorator")
	TaskDecorator taskDecorator()
	{
		return new TaskDecorator()
		{	
			@Override
			public Runnable decorate(Runnable runnable)
			{
				return TracingRunnableWrapper.of(runnable);
			}
		};
	}
	
}
