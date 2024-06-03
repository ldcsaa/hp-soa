package io.github.hpsocket.soa.framework.web.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** <b>动态日志更新监听器</b> */
@Slf4j
public class DynamicLogLevelRefreshEventListener implements ApplicationListener<RefreshScopeRefreshedEvent>, Ordered
{
    @Getter(AccessLevel.PACKAGE)
    @Value("#{${hp.soa.web.app.dynamic-log-levels:{}}}")
    private Map<String, LogLevel> dynamicLogLevels;

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event)
    {        
        Map<String, LogLevel> preDynamicLogLevels = AppConfigHolder.getDynamicLogLevels();
        Map<String, LogLevel> curDynamicLogLevels = dynamicLogLevels;
        
        if(GeneralHelper.isNullOrEmpty(curDynamicLogLevels))
        {
            if(GeneralHelper.isNotNullOrEmpty(preDynamicLogLevels))
            {
                log.info("application dynamic log levels switch -> (to default)");
                
                AppConfigHolder.setDynamicLogLevels(curDynamicLogLevels);
            }
        }
        else if(!curDynamicLogLevels.equals(preDynamicLogLevels))
        {
            log.info("application dynamic log levels switch -> {}", curDynamicLogLevels);
            
            AppConfigHolder.setDynamicLogLevels(curDynamicLogLevels);
            curDynamicLogLevels.forEach((k, v) -> WebServerHelper.getLoggingSystem().setLogLevel(k, v));
        }
    }

    @Override
    public int getOrder()
    {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
