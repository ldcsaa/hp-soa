package io.github.hpsocket.soa.starter.rabbitmq.common.listener;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitmqrSwitchReadOnlyListener implements ApplicationListener<ReadOnlyEvent>, Ordered
{
	@Autowired(required = false)
	private RabbitListenerEndpointRegistry registry;

	@Override
	public void onApplicationEvent(ReadOnlyEvent event)
	{
		if(registry == null)
			return;
		
		boolean readOnly = event.isReadOnly();
		
		log.info("receive read-only switch event (read-only: {}), prepare to {} all consumers)", readOnly, readOnly ? "STOP" : "START");
		
		if(readOnly)
			registry.stop();
		else
			registry.start();
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
