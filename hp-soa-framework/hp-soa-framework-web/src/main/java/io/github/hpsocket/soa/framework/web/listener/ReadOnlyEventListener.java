package io.github.hpsocket.soa.framework.web.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadOnlyEventListener implements ApplicationListener<RefreshEvent>, Ordered
{
	@Autowired
	IAppProperties appProperties;
	
	@PostConstruct
	public void postConstruct()
	{
		if(appProperties.isReadOnly())
			log.info("application state -> (read-only: true)");
	}

	@Override
	public void onApplicationEvent(RefreshEvent event)
	{
		boolean preVal = AppConfigHolder.isReadOnly();
		boolean curVal = appProperties.isReadOnly();
		
		if(preVal != curVal)
		{
			AppConfigHolder.setReadOnly(curVal);
			SpringContextHolder.getApplicationContext().publishEvent(new ReadOnlyEvent(event, curVal));
			
			log.info("application state switch -> (read-only: {})", curVal);
		}
	}

	@Override
	public int getOrder()
	{
		return Ordered.LOWEST_PRECEDENCE;
	}
}
