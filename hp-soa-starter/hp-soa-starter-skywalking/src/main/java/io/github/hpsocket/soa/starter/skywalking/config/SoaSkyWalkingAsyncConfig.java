
package io.github.hpsocket.soa.starter.skywalking.config;

import io.github.hpsocket.soa.framework.web.propertries.IAsyncProperties;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.skywalking.async.TracingAsyncThreadPoolExecutor;
import io.github.hpsocket.soa.starter.web.config.WebConfig;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** <b>HP-SOA Skywalking Async 配置</b> */
@AutoConfiguration
@ConditionalOnClass({TraceContext.class, WebConfig.class})
@AutoConfigureBefore(WebConfig.class)
public class SoaSkyWalkingAsyncConfig
{
    public static final String asyncThreadPoolExecutorBeanName = WebConfig.asyncThreadPoolExecutorBeanName;

    /** 异步线程池（注入 traceId） */
    @Bean(asyncThreadPoolExecutorBeanName)
    @ConditionalOnProperty(name = "hp.soa.web.async.enabled", havingValue = "true", matchIfMissing = true)
    TracingAsyncThreadPoolExecutor asyncThreadPoolExecutor(IAsyncProperties asyncProperties)
    {
        RejectedExecutionHandler rjh  = WebServerHelper.parseRejectedExecutionHandler(asyncProperties.getRejectionPolicy());
        BlockingQueue<Runnable> queue = asyncProperties.getQueueCapacity() == 0 
                                        ? new SynchronousQueue<>() 
                                        : new LinkedBlockingDeque<>(asyncProperties.getQueueCapacity());

        AtomicInteger THREAD_NUMBER = new AtomicInteger();
        TracingAsyncThreadPoolExecutor executor = new TracingAsyncThreadPoolExecutor(
                                            asyncProperties.getCorePoolSize(),
                                            asyncProperties.getMaxPoolSize(),
                                            asyncProperties.getKeepAliveSeconds(),
                                            TimeUnit.SECONDS,
                                            queue,
                                            (r) -> {Thread t = new Thread(r);
                                                t.setName("HP-SOA-TRACING-ASYNC-" + THREAD_NUMBER.incrementAndGet());
                                                t.setPriority(Thread.NORM_PRIORITY);
                                                t.setDaemon(false);
                                                return t;},
                                            rjh);
        
        executor.allowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
        
        return executor;
    }
    
}
