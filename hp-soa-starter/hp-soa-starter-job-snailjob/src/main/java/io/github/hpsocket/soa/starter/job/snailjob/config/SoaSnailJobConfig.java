
package io.github.hpsocket.soa.starter.job.snailjob.config;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.starter.EnableSnailJob;

import io.github.hpsocket.soa.starter.job.snailjob.aspect.SnailJobMdcInspector;
import io.github.hpsocket.soa.starter.job.snailjob.aspect.SnailJobTracingInspector;
import io.github.hpsocket.soa.starter.job.snailjob.executor.JobInvoker;

/** <b>HP-SOA SnailJob 配置</b> */
@AutoConfiguration
@EnableSnailJob
@ConditionalOnClass(JobExecutor.class)
@ConditionalOnProperty(name = "snail-job.enabled", matchIfMissing = true)
public class SoaSnailJobConfig
{
    @Bean
    JobInvoker jobInvoker()
    {
        return new JobInvoker();
    }
    
    @Bean
    @ConditionalOnClass(Trace.class)
    SnailJobTracingInspector snailTracingInspector()
    {
        return new SnailJobTracingInspector();
    }
    
    @Bean
    SnailJobMdcInspector SnailJobMdcInspector()
    {
        return new SnailJobMdcInspector();
    }
    
}
