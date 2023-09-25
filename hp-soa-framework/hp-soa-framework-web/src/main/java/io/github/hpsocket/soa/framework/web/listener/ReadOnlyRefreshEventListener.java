package io.github.hpsocket.soa.framework.web.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import lombok.extern.slf4j.Slf4j;

/** <b>{@linkplain ReadOnlyEvent} 事件动态更新触发器</b><br>
 * 触发时机：动态修改应用程序的 <i>${hp.soa.web.app.read-only}</i> 配置时，（此时 {@linkplain ReadOnlyEvent#isInitial()} 为 faise）
 */
@Slf4j
public class ReadOnlyRefreshEventListener implements ApplicationListener<RefreshEvent>, Ordered
{
    @Autowired
    IAppProperties appProperties;

    @Override
    public void onApplicationEvent(RefreshEvent event)
    {
        boolean preVal = AppConfigHolder.isReadOnly();
        boolean curVal = appProperties.isReadOnly();
        
        if(preVal != curVal)
        {
            log.info("application state switch -> (read-only: {})", curVal);
            
            AppConfigHolder.setReadOnly(curVal);
            SpringContextHolder.publishEvent(new ReadOnlyEvent(event, curVal, false));
        }
    }

    @Override
    public int getOrder()
    {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
