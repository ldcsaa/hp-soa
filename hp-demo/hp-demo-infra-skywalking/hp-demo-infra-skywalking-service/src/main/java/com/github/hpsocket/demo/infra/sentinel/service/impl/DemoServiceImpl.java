package com.github.hpsocket.demo.infra.sentinel.service.impl;

import java.util.concurrent.CompletableFuture;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.github.hpsocket.demo.infra.sentinel.service.DemoService;
import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.starter.skywalking.async.TracingSupplierWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RefreshScope
@DubboService
public class DemoServiceImpl implements DemoService
{
	@Value("${aaa.bbb}")
	Integer val;
	
	@Override
	public String sayHello(String name)
	{
		MdcAttr mdc = MdcAttr.fromMdc();
		log.info("MDC: {}", mdc.getCtxMap());
		log.info("Trace: {}, {}, {}", TraceContext.traceId(), TraceContext.segmentId(), TraceContext.spanId());
		
		if(name.length() < 4)
			throw new DemoException("Just test exception: name length < 4");
		
		CompletableFuture.supplyAsync(TracingSupplierWrapper.of(() -> {
			GeneralHelper.waitFor(1000);
			log.info("Async MDC: {}", mdc.getCtxMap());
			log.info("Async Trace: {}, {}, {}", TraceContext.traceId(), TraceContext.segmentId(), TraceContext.spanId());			
			return "OK";
		}));
		
		return "Hello Mr. " + name; 
	}

	@SuppressWarnings("serial")
	public static class DemoException extends RuntimeException
	{
		public DemoException()
		{
			super();
		}
		
		public DemoException(String msg)
		{
			super(msg);
		}
	}
}
