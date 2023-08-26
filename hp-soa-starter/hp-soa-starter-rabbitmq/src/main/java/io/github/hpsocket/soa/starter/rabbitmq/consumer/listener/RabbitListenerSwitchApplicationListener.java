package io.github.hpsocket.soa.starter.rabbitmq.consumer.listener;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitListenerSwitchApplicationListener implements ApplicationListener<ReadOnlyEvent>
{
	@Autowired
	private RabbitListenerEndpointRegistry registry;

	@Override
	public void onApplicationEvent(ReadOnlyEvent event)
	{
		boolean readOnly = event.isReadOnly();
		
		log.info("receive read-only switch event (read-only: {}), prepare to {} all consumers)", readOnly, readOnly ? "STOP" : "START");
		
		if(readOnly)
			registry.stop();
		else
			registry.start();
	}

}
