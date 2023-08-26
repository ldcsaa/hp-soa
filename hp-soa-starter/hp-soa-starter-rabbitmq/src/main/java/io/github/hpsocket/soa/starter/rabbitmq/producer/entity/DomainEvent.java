
package io.github.hpsocket.soa.starter.rabbitmq.producer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
	 * 关联请求Id
	 */
	private String requestId;

	/**
	 * 消息体
	 */
	private String msg;

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
}
