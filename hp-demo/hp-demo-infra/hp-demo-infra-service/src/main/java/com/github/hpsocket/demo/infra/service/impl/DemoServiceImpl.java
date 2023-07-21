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
		
		return "Hello Mr. " + name; 
	}

}
