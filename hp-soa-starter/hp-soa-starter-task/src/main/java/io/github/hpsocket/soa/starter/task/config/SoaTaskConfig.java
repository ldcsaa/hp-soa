
package io.github.hpsocket.soa.starter.task.config;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.task.aspect.ScheduledTraceIdInspector;
import io.github.hpsocket.soa.starter.task.properties.SoaTaskProperties;
import io.github.hpsocket.soa.starter.task.properties.SoaTaskProperties.Scheduling;

/** <b>HP-SOA Task 配置</b> */
@EnableAsync
@EnableScheduling
@AutoConfiguration
@Import(ScheduledTraceIdInspector.class)
@EnableConfigurationProperties({SoaTaskProperties.class})
@ConditionalOnProperty(name = "spring.task.enabled", matchIfMissing = true)
public class SoaTaskConfig
{
    public static final String mdcTaskDecoratorBeanName = "mdcTaskDecorator";
    
    private SoaTaskProperties soaTaskProperties;
    
    public SoaTaskConfig(SoaTaskProperties soaTaskProperties)
    {
        this.soaTaskProperties = soaTaskProperties;
    }
    
    /** Task 任务装饰器（注入 {@linkplain org.slf4j.MDC MDC} 调用链跟踪信息）*/
    @Bean(mdcTaskDecoratorBeanName)
    @ConditionalOnMissingBean(name = mdcTaskDecoratorBeanName)
    TaskDecorator taskDecorator()
    {
        return new TaskDecorator()
        {    
            @Override
            public Runnable decorate(Runnable runnable)
            {
                MdcAttr mdcAttr = MdcAttr.fromMdc();
                
                return new Runnable()
                {
                    @Override
                    public void run()
                    {
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
        };
    }
    
    @Bean
    TaskExecutorCustomizer taskExecutorCustomizer()
    {
        return new TaskExecutorCustomizer()
        {
            @Override
            public void customize(ThreadPoolTaskExecutor taskExecutor)
            {
                String executionRejectionPolicy = soaTaskProperties.getExecution().getPool().getRejectionPolicy();
                RejectedExecutionHandler rjh = WebServerHelper.parseRejectedExecutionHandler(executionRejectionPolicy, "CALLER_RUNS");                
                taskExecutor.setRejectedExecutionHandler(rjh);
            }
        };
    }
    
    @Bean
    @SuppressWarnings("serial")
    ThreadPoolTaskScheduler taskScheduler()
    {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler()
        {
            @Override
            protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler)
            {
                return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler)
                {
                    @Override
                    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
                    {
                        return super.schedule(wrapCommand(command), delay, unit);
                    }
                    
                    @Override
                    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
                    {
                        return super.schedule(wrapCommand(callable), delay, unit);
                    }
                    
                    @Override
                    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
                    {
                        return super.scheduleAtFixedRate(wrapCommand(command), initialDelay, period, unit);
                    }
                    
                    @Override
                    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
                    {
                        return super.scheduleWithFixedDelay(wrapCommand(command), initialDelay, delay, unit);
                    }
                    
                    private Runnable wrapCommand(Runnable r)
                    {
                        return new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                MdcAttr mdcAttr = WebServerHelper.createMdcAttr();
                                
                                try
                                {
                                    mdcAttr.putMdc();
                                    r.run();
                                }
                                finally
                                {
                                    mdcAttr.removeMdc();
                                }
                            }
                        };
                    }
                    
                    private <T> Callable<T> wrapCommand(Callable<T> t)
                    {
                        return new Callable<>()
                        {
                            @Override
                            public T call()  throws Exception
                            {
                                MdcAttr mdcAttr = WebServerHelper.createMdcAttr();
                                
                                try
                                {
                                    mdcAttr.putMdc();
                                    return t.call();
                                }
                                finally
                                {
                                    mdcAttr.removeMdc();
                                }
                            }
                        };
                    }
                };
            }
        };
        
        Scheduling scheduling = soaTaskProperties.getScheduling();
        
        scheduler.setThreadNamePrefix(scheduling.getThreadNamePrefix());
        scheduler.setPoolSize(scheduling.getPool().getSize());
        scheduler.setWaitForTasksToCompleteOnShutdown(scheduling.getShutdown().isAwaitTermination());
        scheduler.setAwaitTerminationMillis(scheduling.getShutdown().getAwaitTerminationPeriod());

        String schedulingRejectionPolicy = scheduling.getPool().getRejectionPolicy();
        RejectedExecutionHandler rjh = WebServerHelper.parseRejectedExecutionHandler(schedulingRejectionPolicy, "ABORT");
        scheduler.setRejectedExecutionHandler(rjh);

        return scheduler;
    }

}
