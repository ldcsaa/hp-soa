package io.github.hpsocket.soa.starter.rabbitmq.consumer.listener;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitmqReadOnlyEventListener implements ApplicationListener<ReadOnlyEvent>, Ordered
{
	@Autowired
	private RabbitListenerEndpointRegistry registry;

	@Override
	public void onApplicationEvent(ReadOnlyEvent event)
	{
		boolean readOnly = event.isReadOnly();
		boolean initial  =event.isInitial();
		
		if(!initial)
		{
			log.info("receive read-only switch event (read-only: {}), prepare to {} all consumers", readOnly, readOnly ? "STOP" : "START");
			
			if(readOnly)
				registry.stop();
			else
				registry.start();			
		}
		else if(readOnly && registry.isRunning())
		{
			log.info("application is read-only, then STOP RabbitListenerEndpointRegistry");

			registry.stop();
		}
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
