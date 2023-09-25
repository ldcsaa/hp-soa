
package io.github.hpsocket.soa.starter.rabbitmq.producer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.rabbit.stream.support.StreamMessageProperties;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.data.mysql.entity.BaseLogicDeleteEntity;
import io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant;
import lombok.Getter;
import lombok.Setter;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

/** <b>领域事件实体</b><br>
 * 对应领域事件数据库表，应用程序可以继承该实体，扩展自定义字段
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class DomainEvent extends BaseLogicDeleteEntity implements Serializable
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
     * 交换机（领域事件的交换机是fanout类型）
     */
    private String exchange;

    /**
     * 路由键（领域事件的路由键为空）
     */
    private String routingKey;

    /**
     * 事件唯一Id
     */
    private String msgId;

    /**
     * 消息体
     */
    private String msg;

    /**
     * 关联请求Id
     */
    private String sourceRequestId;

    /**
     * 发送标记（{@linkplain RabbitmqConstant#SF_NOT_SEND SF_NOT_SEND} - 未发送，{@linkplain RabbitmqConstant#SF_SENDING SF_SENDING} - 正在发送，{@linkplain RabbitmqConstant#SF_SEND_SUCC SF_SEND_SUCC} - 发送成功，{@linkplain RabbitmqConstant#SF_SEND_FAIL SF_SEND_FAIL} - 发送失败）
     */
    private Integer sendFlag;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 最后发送时间
     */
    private LocalDateTime lastSendTime;

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
    
    public Message toMessage()
    {
        return toMessage(null, null);
    }
    
    public Message toMessage(MessageProperties messageProperties)
    {
        return toMessage(messageProperties, null);
    }
    
    public Message toMessage(String correlationId)
    {
        return toMessage(null, correlationId);
    }
    
    public Message toMessage(MessageProperties messageProperties, String correlationId)
    {
        MessageBuilder builder = MessageBuilder.withBody(msg.getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ));
        
        if(messageProperties != null)
            builder.andProperties(messageProperties);
        
        if(GeneralHelper.isStrNotEmpty(correlationId))
            builder.setCorrelationId(correlationId);
        
        return builder
            .setContentEncoding(WebServerHelper.DEFAULT_CHARSET)
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .setMessageId(msgId)
            
            .setHeader(HEADER_DOMAIN_NAME, domainName)
            .setHeader(HEADER_EVENT_NAME, eventName)
            .setHeader(HEADER_MSG_ID, msgId)
            .setHeader(HEADER_SOURCE_REQUEST_ID, sourceRequestId)
            .build();
    }
    
    public Message toStreamMessage()
    {
        return toStreamMessage(null, null);
    }
    
    public Message toStreamMessage(StreamMessageProperties streamMessageProperties)
    {
        return toStreamMessage(streamMessageProperties, null);
    }
    
    public Message toStreamMessage(String correlationId)
    {
        return toStreamMessage(null, correlationId);            
    }
    
    public Message toStreamMessage(StreamMessageProperties streamMessageProperties, String correlationId)
    {
        if(streamMessageProperties == null)
            streamMessageProperties = new StreamMessageProperties();
        
        streamMessageProperties.setGroupId(domainName);
        
        MessageBuilder builder = MessageBuilder.withBody(msg.getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ));
        builder.andProperties(streamMessageProperties);
        
        if(GeneralHelper.isStrNotEmpty(correlationId))
            builder.setCorrelationId(correlationId);
        
        return builder
            .setContentEncoding(WebServerHelper.DEFAULT_CHARSET)
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .setMessageId(msgId)
            .setHeader(HEADER_DOMAIN_NAME, domainName)
            .setHeader(HEADER_EVENT_NAME, eventName)
            .setHeader(HEADER_MSG_ID, msgId)
            .setHeader(HEADER_SOURCE_REQUEST_ID, sourceRequestId)
            .build();
    }
    
}
