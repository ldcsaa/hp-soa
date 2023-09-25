package io.github.hpsocket.soa.starter.web.bootstrap;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.server.init.ServerInitializer;

public class SoaSpringApplicationRunListener implements SpringApplicationRunListener, Ordered
{
    @Override
    public void contextPrepared(ConfigurableApplicationContext context)
    {
        ServerInitializer.switchOverAllLogs();
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }
    
}
