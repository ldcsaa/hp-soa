package io.github.hpsocket.soa.starter.rabbitmq.consumer.listener;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import lombok.extern.slf4j.Slf4j;

/** <b>{@linkplain ReadOnlyEvent} 应用程序事件处理器</b><br>
 * 当应用程序为只读时，关闭所有消息监听器，不接收任何消息
 */
@Slf4j
public class RabbitmqReadOnlyEventListener implements ApplicationListener<ReadOnlyEvent>, Ordered
{
    @Autowired
    private RabbitListenerEndpointRegistry registry;

    @Override
    public void onApplicationEvent(ReadOnlyEvent event)
    {
        synchronized(RabbitmqReadOnlyEventListener.class)
        {
            boolean readOnly = event.isReadOnly();
            boolean initial  = event.isInitial();
            
            if(!initial)
                log.info("receive read-only switch event (read-only: {}), prepare to {} RabbitListenerEndpointRegistry", readOnly, readOnly ? "STOP" : "START");
            else if(readOnly)
                log.info("application is read-only, then STOP RabbitListenerEndpointRegistry");
            
            doSwitch(readOnly);
        }
    }
    
    private void doSwitch(boolean readOnly)
    {
        boolean running = registry.isRunning();
        
        if(readOnly && running)
            registry.stop();
        else if(!readOnly && !running)
            registry.start();
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
