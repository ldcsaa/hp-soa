package io.github.hpsocket.soa.framework.web.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadOnlyContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent>, Ordered
{
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		boolean readOnly = AppConfigHolder.isReadOnly();
		
		if(readOnly)
			log.info("application initial state -> (read-only: {})", readOnly);
		
		SpringContextHolder.publishEvent(new ReadOnlyEvent(event, readOnly, true));
	}

	@Override
	public int getOrder()
	{
		return Ordered.LOWEST_PRECEDENCE;
	}
}
