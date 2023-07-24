package com.github.hpsocket.demo.infra.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.ibatis.exceptions.PersistenceException;

import com.github.hpsocket.demo.infra.service.DemoService;
import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;

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
		
		if(GeneralHelper.isStrEmpty(name))
			throw new PersistenceException("just test exception: name is empty");
		
		return "Hello Mr. " + name; 
	}

}
