
package io.github.hpsocket.soa.starter.rabbitmq.producer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.Getter;
import lombok.Setter;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

@Getter
@Setter
@SuppressWarnings("serial")
public class DomainEvent implements Serializable
{
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 领域名称
	 * eg: dms.sales.order
	 */
	private String domainName;

	/**
	 * 事件名称
	 * eg: order.paid
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
	 * 发送标记（0 - 未发送，1 - 正在发送，2 - 发送成功，3 - 发送失败）
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

	/**
	 * 逻辑删除标记
	 */
	@TableLogic
	private Byte deleted;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime updateTime;
	
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
		return toMessage(null);
	}
	
	public Message toMessage(MessageProperties messageProperties)
	{
		MessageBuilder builder = MessageBuilder.withBody(msg.getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ));
		
		if(messageProperties != null)
			builder.copyProperties(messageProperties);
		
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
