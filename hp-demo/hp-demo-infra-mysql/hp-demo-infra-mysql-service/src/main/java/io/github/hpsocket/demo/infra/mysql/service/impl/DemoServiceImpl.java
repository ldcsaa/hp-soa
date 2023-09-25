package io.github.hpsocket.demo.infra.mysql.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import io.github.hpsocket.demo.infra.mysql.service.DemoService;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RefreshScope
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
