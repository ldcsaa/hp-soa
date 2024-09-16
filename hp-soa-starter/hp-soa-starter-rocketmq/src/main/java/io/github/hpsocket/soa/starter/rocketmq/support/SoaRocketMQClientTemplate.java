package io.github.hpsocket.soa.starter.rocketmq.support;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.slf4j.MDC;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.messaging.MessageHeaders;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleConsumerReceiveConfiguration;
import io.github.hpsocket.soa.starter.rocketmq.consumer.listener.RocketmqReadOnlyEventListener;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate RocketMQClientTemplate} 增强类</b><br>
 * {@linkplain RocketmqReadOnlyEventListener} 是 HP-SOA 的默认 Template<p>
 * <b>Producer 增强：</b>
 * <ol>
 * <li>自动注入 messageId、sourceRequestId 等调用链跟踪信息</li>
 * <li>当应用程序只读时，拦截消息发送</li>
 * </ol>
 * <b>Consumer 增强：</b>
 * <ol>
 * <li>为 Simple Consumer 自动启动消息接收，参考 {@linkplain SoaSimpleConsumerReceiveConfiguration} 和 {@linkplain SoaSimpleConsumerReceiveProperties}
 * </ol>
 */
@Slf4j
public class SoaRocketMQClientTemplate extends RocketMQClientTemplate implements BeanNameAware
{
    public static final int DEFAULT_CONSUMPTION_THREAD_COUNT = 1;
    public static final int DEFAULT_MAX_MESSAGE_NUM          = 16;
    public static final int DEFAULT_INVISIBLE_DURATION       = 15;
    
    private static final int RETRY_WAIT_TIME                 = 5;
    public static final int AWAIT_TERMINATION_TIME           = 15;
    
    @Autowired
    private ConfigurableEnvironment environment;
    @Autowired
    private SoaSimpleConsumerReceiveProperties defaultReceiveProperties;
    
    @Getter
    @Setter
    private SoaSimpleConsumerListener simpleConsumerListener;
    @Getter
    @Setter
    private boolean autoAck = true;
    
    private ExecutorService executorService;
    private volatile boolean stopped;

    private String beanName;

    @Override
    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }
    
    @EventListener
    public void onApplicationContextRefreshedEvent(ContextRefreshedEvent event)
    {
        if(this.getSimpleConsumerBuilder() == null)
            return;
        
        SoaSimpleConsumerReceiveProperties receiveProperties = parseReceiveProperties();
        
        if(!receiveProperties.isAutoStart())
            return;
        
        if(this.getSimpleConsumerListener() == null)
            throw new IllegalStateException("No simpleConsumerListener for bean: '" + beanName + "'");
        
        int threadCount = receiveProperties.getConsumptionThreadCount();
        executorService = Executors.newFixedThreadPool(threadCount);
        
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(new ReceiveTask(receiveProperties)));
    }
    
    @EventListener
    public void onApplicationContextClosedEvent(ContextClosedEvent event)
    {
        stopped = true;
        
        if(executorService != null)
        {
            try
            {
                executorService.shutdown();
                executorService.awaitTermination(AWAIT_TERMINATION_TIME, TimeUnit.SECONDS);
            }
            catch(Exception e)
            {
                log.error("(beanName: {}) exception occur while await termination -> {}", beanName, e.getMessage(), e);               
            }
        }
    }

    @Override
    protected Map<String, Object> processHeadersToSend(Map<String, Object> headers)
    {
        WebServerHelper.assertAppIsNotReadOnly();
        
        if(headers == null)
            headers = new HashMap<>();
        
        String messageId = (String)headers.get(RocketmqConstant.HEADER_MSG_ID);
        String sourceRequestId = (String)headers.get(RocketmqConstant.HEADER_SOURCE_REQUEST_ID);
        
        if(GeneralHelper.isStrEmpty(messageId))
        {
            UUID id = (UUID)headers.get(MessageHeaders.ID);
            
            if(id != null)
                messageId = id.toString();
            else
                messageId = IdGenerator.nextIdStr();
            
            headers.put(RocketmqConstant.HEADER_MSG_ID, messageId);
        }
        
        if(GeneralHelper.isStrEmpty(sourceRequestId))
            headers.put(RocketmqConstant.HEADER_SOURCE_REQUEST_ID, MDC.get(MdcAttr.MDC_REQUEST_ID_KEY));
        
        return headers;
    }
    
    private SoaSimpleConsumerReceiveProperties parseReceiveProperties()
    {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(this);
        SoaSimpleConsumerReceiveConfiguration annotation = AnnotationUtils.findAnnotation(clazz, SoaSimpleConsumerReceiveConfiguration.class);
        
        if(annotation == null)
            return defaultReceiveProperties;
        
        SoaSimpleConsumerReceiveProperties receiveProperties = defaultReceiveProperties.clone();
        
        String value = environment.resolvePlaceholders(annotation.autoStart());
        if(GeneralHelper.isStrNotEmpty(value)) receiveProperties.setAutoStart(Boolean.parseBoolean(value));
        value = environment.resolvePlaceholders(annotation.consumptionThreadCount());
        if(GeneralHelper.isStrNotEmpty(value)) receiveProperties.setConsumptionThreadCount(Integer.parseInt(value));
        value = environment.resolvePlaceholders(annotation.maxMessageNum());
        if(GeneralHelper.isStrNotEmpty(value)) receiveProperties.setMaxMessageNum(Integer.parseInt(value));
        value = environment.resolvePlaceholders(annotation.invisibleDuration());
        if(GeneralHelper.isStrNotEmpty(value)) receiveProperties.setInvisibleDuration(Integer.parseInt(value));
        
        return receiveProperties;
    }

    private class ReceiveTask implements Runnable
    {
        private SoaSimpleConsumerReceiveProperties receiveProperties;

        public ReceiveTask(SoaSimpleConsumerReceiveProperties receiveProperties)
        {
            this.receiveProperties = receiveProperties;
        }

        @Override
        public void run()
        {
            int maxMessageNum       = receiveProperties.getMaxMessageNum();
            int invisibleDuration   = receiveProperties.getInvisibleDuration();
            String consumerGroup    = SoaRocketMQClientTemplate.this.getSimpleConsumer().getConsumerGroup();
            
            log.info("({}) start receive -> (consumerGroup: {}, maxMessageNum: {}, invisibleDuration: {})", beanName, consumerGroup, maxMessageNum, invisibleDuration);
            
            while(!stopped)
            {
                try
                {
                    final List<MessageView> messages = receive(maxMessageNum, Duration.ofSeconds(invisibleDuration));
                    
                    for(MessageView message : messages)
                    {
                        try
                        {
                            simpleConsumerListener.consume(message, SoaRocketMQClientTemplate.this);
                            if(autoAck) ack(message);
                        }
                        catch(Exception e)
                        {
                            log.error(  "exception occur while process message (beanName: {}, topic: {}, consumerGroup: {}, messageId: {}) -> {}", 
                                        beanName, message.getTopic(), consumerGroup, message.getMessageId(), e.getMessage(), e);

                            if(e instanceof ClientException)
                                throw e;
                        }
                    }
                }
                catch(Exception e)
                {
                    String msg = GeneralHelper.isStrNotEmpty(e.getMessage()) ? e.getMessage() : e.getClass().getName();
                    log.error("(beanName: {}) exception occur while receiving message, retry {} seconds later -> {}", beanName, RETRY_WAIT_TIME, msg);
                    
                    GeneralHelper.waitFor(Duration.ofSeconds(RETRY_WAIT_TIME));
                }
            }
            
            log.info("({}) end receive -> (consumerGroup: {}, maxMessageNum: {}, invisibleDuration: {})", beanName, consumerGroup, maxMessageNum, invisibleDuration);
        }
    }
    
}
