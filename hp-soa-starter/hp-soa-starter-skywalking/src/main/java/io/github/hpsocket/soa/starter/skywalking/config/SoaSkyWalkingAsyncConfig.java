
package io.github.hpsocket.soa.starter.skywalking.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.framework.web.propertries.IAsyncProperties;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.skywalking.async.TracingAsyncThreadPoolExecutor;
import io.github.hpsocket.soa.starter.web.config.WebConfig;

/** <b>HP-SOA Skywalking Async 配置</b> */
@AutoConfiguration
@ConditionalOnClass({TraceContext.class, WebConfig.class})
@AutoConfigureBefore(WebConfig.class)
public class SoaSkyWalkingAsyncConfig
{
	/** 异步线程池（注入 traceId） */
    @Bean("asyncThreadPoolExecutor")
    @ConditionalOnProperty(name="hp.soa.web.async.enabled", havingValue="true", matchIfMissing = true)
    public TracingAsyncThreadPoolExecutor asyncThreadPoolExecutor(IAsyncProperties asyncProperties)
    {
		RejectedExecutionHandler rjh  = WebServerHelper.parseRejectedExecutionHandler(asyncProperties.getRejectionPolicy(), "CALLER_RUNS");
		BlockingQueue<Runnable> queue = asyncProperties.getQueueCapacity() == 0 
										? new SynchronousQueue<>() 
										: new LinkedBlockingDeque<>(asyncProperties.getQueueCapacity());
		
		TracingAsyncThreadPoolExecutor executor = new TracingAsyncThreadPoolExecutor(
											asyncProperties.getCorePoolSize(),
											asyncProperties.getMaxPoolSize(),
											asyncProperties.getKeepAliveSeconds(),
											TimeUnit.SECONDS,
											queue,
											rjh);
		
		executor.allowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
    	
		return executor;
    }
	
}
