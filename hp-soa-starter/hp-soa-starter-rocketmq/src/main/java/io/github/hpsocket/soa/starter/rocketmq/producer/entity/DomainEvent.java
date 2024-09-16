
package io.github.hpsocket.soa.starter.rocketmq.producer.entity;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.Getter;
import lombok.Setter;

/** <b>领域事件实体</b><br>
 * 通用领域时间封装类，应用程序可以继承该实体，扩展自定义字段
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class DomainEvent<T> implements Serializable
{
    /**
     * 领域名称
     */
    private String domainName;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 主题
     */
    private String topic;

    /**
     * 消息标签
     */
    private String tag;

    /**
     * 消息时间戳
     */
    private Long msgTimestamp;

    /**
     * 消息键
     */
    private String keys;

    /**
     * 消息头
     */
    private Map<String, Object> msgHeaders;

    /**
     * 事件唯一Id
     */
    private String msgId;

    /**
     * 消息体
     */
    private T msg;

    /**
     * 关联请求Id
     */
    private String sourceRequestId;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 关联数据ID
     */
    private String correlationId;

    public DomainEvent()
    {
        this(IdGenerator.nextIdStr());
    }
    
    public DomainEvent(String msgId)
    {
        this(msgId, MDC.get(MdcAttr.MDC_REQUEST_ID_KEY));
    }
    
    public DomainEvent(String msgId, String sourceRequestId)
    {
        this.msgId = msgId;
        this.sourceRequestId = sourceRequestId;
    }
    
    public DomainEvent(T msg)
    {
        this();
        this.msg = msg;
    }
    
    public DomainEvent(String msgId, T msg)
    {
        this(msgId);
        this.msg = msg;
    }
    
    public DomainEvent(String msgId, String sourceRequestId, T msg)
    {
        this(msgId, sourceRequestId);
        this.msg = msg;
    }
    
    public String getDestination()
    {
        StringBuilder sb = new StringBuilder(topic);
        
        if(GeneralHelper.isStrNotEmpty(tag))
            sb.append(':').append(tag);
        
        return sb.toString();
    }
    
    public Message<? extends T> toMessage()
    {
        MessageBuilder<? extends T> builder = MessageBuilder.withPayload(msg);
        
        if(GeneralHelper.isStrNotEmpty(domainName))
            builder.setHeader(RocketmqConstant.HEADER_DOMAIN_NAME, domainName);
        if(GeneralHelper.isStrNotEmpty(eventName))
            builder.setHeader(RocketmqConstant.HEADER_EVENT_NAME, eventName);
        if(GeneralHelper.isStrNotEmpty(msgId))
            builder.setHeader(RocketmqConstant.HEADER_MSG_ID, msgId);
        if(GeneralHelper.isStrNotEmpty(sourceRequestId))
            builder.setHeader(RocketmqConstant.HEADER_SOURCE_REQUEST_ID, sourceRequestId);
        if(GeneralHelper.isStrNotEmpty(keys))
            builder.setHeader(RocketmqConstant.HEADER_KEYS, keys);  
        if(GeneralHelper.isStrNotEmpty(correlationId))
            builder.setHeader(RocketmqConstant.HEADER_CORRELATION_ID, correlationId);
        if(msgTimestamp != null)
            builder.setHeader(RocketmqConstant.HEADER_MSG_TIMESTAMP, msgTimestamp);  
        if(retries != null)
            builder.setHeader(RocketmqConstant.HEADER_RETRIES, retries);  
        
        if(GeneralHelper.isNotNullOrEmpty(msgHeaders))
        {
            msgHeaders.forEach((k, v) ->
            {
                if(v != null)
                {
                    String value = String.valueOf(v);
                    
                    if(GeneralHelper.isStrNotEmpty(value))
                        builder.setHeader(k, value);
                }
            });
        }
        
        return builder.build();
    }
    
}
