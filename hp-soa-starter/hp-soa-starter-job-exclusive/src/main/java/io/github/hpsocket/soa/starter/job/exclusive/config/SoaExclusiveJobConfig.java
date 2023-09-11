
package io.github.hpsocket.soa.starter.job.exclusive.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.SchedulingTaskExecutor;

import io.github.hpsocket.soa.starter.job.exclusive.aspect.ExclusiveJobInspector;

/** <b>HP-SOA 分布式独占 Job 配置</b> */
@AutoConfiguration
@Import(ExclusiveJobInspector.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@ConditionalOnBean({SchedulingTaskExecutor.class, RedissonClient.class})
public class SoaExclusiveJobConfig
{

}
