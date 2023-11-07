
package io.github.hpsocket.soa.starter.kafka.producer.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.data.mysql.entity.BaseLogicDeleteEntity;
import io.github.hpsocket.soa.starter.kafka.util.KafkaConstant;
import io.github.hpsocket.soa.starter.kafka.util.KafkaHelper;
import lombok.Getter;
import lombok.Setter;

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
     * 主题
     */
    private String topic;

    /**
     * 主题分区
     */
    private Integer topicPartition;

    /**
     * 消息时间戳
     */
    private Long msgTimestamp;

    /**
     * 消息键
     */
    private String msgKey;

    /**
     * 消息头
     */
    private String msgHeaders;

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
     * 发送标记（{@linkplain KafkaConstant#SF_NOT_SEND SF_NOT_SEND} - 未发送，{@linkplain KafkaConstant#SF_SENDING SF_SENDING} - 正在发送，{@linkplain KafkaConstant#SF_SEND_SUCC SF_SEND_SUCC} - 发送成功，{@linkplain KafkaConstant#SF_SEND_FAIL SF_SEND_FAIL} - 发送失败）
     */
    private Integer sendFlag;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 最后发送时间
     */
    private ZonedDateTime lastSendTime;

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
    
    public ProducerRecord<String, String> toProducerRecord()
    {
        return toProducerRecord(null);
    }
    
    public ProducerRecord<String, String> toProducerRecord(String correlationId)
    {
        ProducerRecord<String, String> rc = new ProducerRecord<>(topic, topicPartition, msgTimestamp, msgKey, msg);
        Headers rcHeaders = rc.headers();
        
        KafkaHelper.addHeader(rcHeaders, KafkaConstant.HEADER_DOMAIN_NAME, domainName);
        KafkaHelper.addHeader(rcHeaders, KafkaConstant.HEADER_EVENT_NAME, eventName);
        KafkaHelper.addHeader(rcHeaders, KafkaConstant.HEADER_MSG_ID, msgId);
        KafkaHelper.addHeader(rcHeaders, KafkaConstant.HEADER_SOURCE_REQUEST_ID, sourceRequestId);
        
        if(GeneralHelper.isStrNotEmpty(correlationId))
            rcHeaders.add(KafkaConstant.HEADER_CORRELA_DATA_ID, correlationId.getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ));
        
        if(GeneralHelper.isStrNotEmpty(msgHeaders))
        {
            JSONObject json = JSON.parseObject(msgHeaders);
            json.forEach((k, v) -> KafkaHelper.addHeader(rcHeaders, k, v));
        }
        
        return rc;
    }
    
}
