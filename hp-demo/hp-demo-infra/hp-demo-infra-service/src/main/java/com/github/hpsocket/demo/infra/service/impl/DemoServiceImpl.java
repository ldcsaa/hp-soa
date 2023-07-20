package com.github.hpsocket.demo.infra.service.impl;

import org.apache.dubbo.config.annotation.DubboService;

import com.github.hpsocket.demo.infra.service.DemoService;

@DubboService
public class DemoServiceImpl implements DemoService
{

	@Override
	public String sayHello(String name)
	{
		return "Hello Mr. " + name; 
	}

}
