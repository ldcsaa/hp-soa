package io.github.hpsocket.soa.starter.rabbitmq.consumer.listener;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitmqApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent>, Ordered
{
	@Autowired
	private RabbitListenerEndpointRegistry registry;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event)
	{
		boolean readOnly = WebServerHelper.isAppReadOnly();
		
		if(readOnly && registry.isRunning())
		{
			log.info("application is read-only, then STOP RabbitListenerEndpointRegistry");

			registry.stop();
		}
	}

	@Override
	public int getOrder()
	{
		return Ordered.LOWEST_PRECEDENCE;
	}

}
