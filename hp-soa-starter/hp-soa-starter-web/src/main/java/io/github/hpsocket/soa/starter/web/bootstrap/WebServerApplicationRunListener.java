package io.github.hpsocket.soa.starter.web.bootstrap;

import io.github.hpsocket.soa.framework.web.server.init.ServerInitializer;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

public class WebServerApplicationRunListener implements SpringApplicationRunListener, Ordered
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
