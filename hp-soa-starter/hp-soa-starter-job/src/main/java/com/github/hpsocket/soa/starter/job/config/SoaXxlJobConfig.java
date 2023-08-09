
package com.github.hpsocket.soa.starter.job.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
public class SoaXxlJobConfig
{

}
