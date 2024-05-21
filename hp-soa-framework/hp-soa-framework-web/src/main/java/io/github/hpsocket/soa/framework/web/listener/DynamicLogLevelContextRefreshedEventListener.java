package io.github.hpsocket.soa.framework.web.listener;

import java.util.Map;

import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.extern.slf4j.Slf4j;

/** <b>动态日志初始化监听器</b> */
@Slf4j
public class DynamicLogLevelContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent>, Ordered
{
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        Map<String, LogLevel> dynamicLogLevels = SpringContextHolder.getBean(DynamicLogLevelRefreshEventListener.class).getDynamicLogLevels();
        
        AppConfigHolder.setDynamicLogLevels(dynamicLogLevels);
        
        if(GeneralHelper.isNotNullOrEmpty(dynamicLogLevels))
        {
            log.info("application initial dynamic log levels -> {}", dynamicLogLevels);
            
            dynamicLogLevels.forEach((k, v) -> WebServerHelper.getLoggingSystem().setLogLevel(k, v));
        }
    }

    @Override
    public int getOrder()
    {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
