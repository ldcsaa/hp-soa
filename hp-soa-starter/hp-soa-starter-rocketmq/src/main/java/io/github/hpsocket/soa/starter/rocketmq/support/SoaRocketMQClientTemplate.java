package io.github.hpsocket.soa.starter.rocketmq.support;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.Getter;
import lombok.Setter;

public class SoaRocketMQClientTemplate extends RocketMQClientTemplate
{
    public static final int DEFAULT_CONSUMPTION_THREAD_COUNT = 1;
    public static final int DEFAULT_MAX_MESSAGE_NUM = 16;
    public static final int DEFAULT_INVISIBLE_DURATION = 15;
    
    private int consumptionThreadCount = DEFAULT_CONSUMPTION_THREAD_COUNT;
    private int maxMessageNum = DEFAULT_MAX_MESSAGE_NUM;
    private int invisibleDuration = DEFAULT_INVISIBLE_DURATION;
    
    private ExecutorService executorService;
    private volatile boolean stopped;
    
    @Getter
    @Setter
    private SoaRocketMQSimpleConsumerListener simpleConsumerListener;
    
    @EventListener
    public void onApplicationContextRefreshedEvent(ContextRefreshedEvent event)
    {
        if(this.getSimpleConsumerBuilder() == null)
            return;
        
        
        
        if(this.getSimpleConsumerListener() == null)
            throw new IllegalStateException("No simpleConsumerListener for this SoaRocketMQClientTemplate");
        
        System.out.println("onApplicationContextRefreshedEvent:" + event.toString());
    }
    
    @EventListener
    public void onApplicationContextClosedEvent(ContextClosedEvent event)
    {
        System.out.println("onApplicationContextClosedEvent:" + event.toString());
    }

    @Override
    protected Map<String, Object> processHeadersToSend(Map<String, Object> headers)
    {
        WebServerHelper.assertAppIsNotReadOnly();
        
        if(headers == null)
            headers = new HashMap<>();
        
        return headers;
    }
}
