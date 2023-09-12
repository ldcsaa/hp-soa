package io.github.hpsocket.soa.starter.task.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.task")
@ConditionalOnProperty(name = "spring.task.enabled", matchIfMissing = true)
public class SoaTaskProperties
{
	private boolean enabled = true;
	
	@NestedConfigurationProperty
	Execution execution = new Execution();
	@NestedConfigurationProperty
	Scheduling scheduling = new Scheduling();
	
	
	@Getter
	@Setter
	public static class Execution
	{
		private String threadNamePrefix = "task-";
		
		private Pool pool = new Pool();
		private Shutdown shutdown = new Shutdown();
		
		@Getter
		@Setter
		public static class Pool
		{
			private int coreSize = 8;
			private int maxSize = 24;
			private int queueCapacity = 1000;
			private int keepAlive = 60;
			private boolean allowCoreThreadTimeout = true;
		    private String rejectionPolicy = "CALLER_RUNS";
		}
		
		@Getter
		@Setter
		public static class Shutdown
		{
			private boolean awaitTermination = false;
			private int awaitTerminationPeriod = 5;
		}
	}
	
	@Getter
	@Setter
	public static class Scheduling
	{
		private String threadNamePrefix = "scheduling-";
		
		private Pool pool = new Pool();
		private Shutdown shutdown = new Shutdown();

		@Getter
		@Setter
		public static class Pool
		{
			private int size = 1;
		    private String rejectionPolicy = "ABORT";
		}
		
		@Getter
		@Setter
		public static class Shutdown
		{
			private boolean awaitTermination = false;
			private int awaitTerminationPeriod = 5;
		}
	}
}
