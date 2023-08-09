
package com.github.hpsocket.soa.starter.job.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import com.github.hpsocket.soa.starter.task.config.SoaTaskConfig;

@AutoConfiguration
@ConditionalOnBean(SoaTaskConfig.class)
public class SoaExclusiveJobConfig
{
	
}
