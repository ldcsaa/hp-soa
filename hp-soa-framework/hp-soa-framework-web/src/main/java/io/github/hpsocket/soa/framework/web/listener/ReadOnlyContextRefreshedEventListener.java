package io.github.hpsocket.soa.framework.web.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

/** <b>{@linkplain ReadOnlyEvent} 事件初始触发器</b><br>
 * 触发时机：应用程序启动时，跟随 {@linkplain org.springframework.context.event.ContextRefreshedEvent ContextRefreshedEvent} 事件之后 （此时 {@linkplain ReadOnlyEvent#isInitial()} 为 true）
 */
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
