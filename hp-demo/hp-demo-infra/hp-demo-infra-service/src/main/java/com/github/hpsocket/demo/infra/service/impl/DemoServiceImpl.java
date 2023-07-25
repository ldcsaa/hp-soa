package com.github.hpsocket.demo.infra.service.impl;

import org.apache.dubbo.config.annotation.DubboService;

import com.github.hpsocket.demo.infra.service.DemoService;
import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DubboService
public class DemoServiceImpl implements DemoService
{
	@Override
	public String sayHello(String name)
	{
		MdcAttr mdc = MdcAttr.fromMdc();
		log.info("MDC: {}", mdc.getCtxMap());
		
		if(name.length() < 4)
			throw new DemoException("Just test exception: name length < 4");
		
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
