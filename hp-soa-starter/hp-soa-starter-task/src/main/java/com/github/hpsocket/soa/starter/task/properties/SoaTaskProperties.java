package com.github.hpsocket.soa.starter.task.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.task")
@ConditionalOnProperty(name = "spring.task.enabled", matchIfMissing = true)
public class SoaTaskProperties
{
	private boolean enabled = true;
	Execution execution = new Execution();
	Scheduling scheduling = new Scheduling();
	
	
	public static class Execution
	{
		
	}
	
	public static class Scheduling
	{
		
	}
}
