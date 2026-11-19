package io.github.hpsocket.soa.starter.rocketmq.support;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleConsumerReceiveConfiguration;
import io.github.hpsocket.soa.starter.rocketmq.consumer.listener.RocketmqReadOnlyEventListener;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.apache.rocketmq.client.support.RocketMQMessageConverter;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration.*;

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

    public SoaRocketMQClientTemplate()
    {
        ProducerBuilder producerBuilder = SpringContextHolder.getBean(PRODUCER_BUILDER_BEAN_NAME, ProducerBuilder.class, false);
        SimpleConsumerBuilder simpleConsumerBuilder = SpringContextHolder.getBean(SIMPLE_CONSUMER_BUILDER_BEAN_NAME, SimpleConsumerBuilder.class, false);

        init(producerBuilder, simpleConsumerBuilder);
    }

    public SoaRocketMQClientTemplate(ProducerBuilder producerBuilder, SimpleConsumerBuilder simpleConsumerBuilder)
    {
        init(producerBuilder, simpleConsumerBuilder);
    }

    public void init(ProducerBuilder producerBuilder, SimpleConsumerBuilder simpleConsumerBuilder)
    {
        if(producerBuilder == null && simpleConsumerBuilder == null)
            throw new IllegalStateException(String.format("no bean named '%s' or '%s' for target bean '%s' to inject",
                                            PRODUCER_BUILDER_BEAN_NAME, SIMPLE_CONSUMER_BUILDER_BEAN_NAME, this.getClass().getSimpleName()));

        setProducerBuilder(producerBuilder);
        setSimpleConsumerBuilder(simpleConsumerBuilder);

        RocketMQMessageConverter messageConverter = SpringContextHolder.getBean(RocketMQMessageConverter.class, false);
        if(messageConverter != null)
            setMessageConverter(messageConverter.getMessageConverter());
    }

    @Override
    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }
    
    @EventListener
    public void onApplicationContextRefreshedEvent(ContextRefreshedEvent event)
    {
        if(this.getSimpleConsumerBuilder() == null || this.getSimpleConsumerListener() == null)
            return;
        
        SoaSimpleConsumerReceiveProperties receiveProperties = parseReceiveProperties();

        if(!receiveProperties.isAutoStart())
            return;
        
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
