package io.github.hpsocket.soa.framework.web.listener;

import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationListener;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadOnlyEventListener implements ApplicationListener<RefreshEvent>
{
	private boolean readOnly;
	
	@PostConstruct
	public void postConstruct()
	{
		readOnly = WebServerHelper.isAppReadOnly();
		
		if(readOnly)
			log.info("application state -> (read-only: {})", readOnly);
	}

	@Override
	public void onApplicationEvent(RefreshEvent event)
	{
		boolean curVal = WebServerHelper.isAppReadOnly();
		
		if(readOnly != curVal)
		{
			readOnly = curVal;
			SpringContextHolder.getApplicationContext().publishEvent(new ReadOnlyEvent(event, readOnly));
			
			log.info("application state switch -> (read-only: {})", readOnly);
		}
	}
}
