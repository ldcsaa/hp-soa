package io.github.hpsocket.soa.starter.rabbitmq.producer.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;

/** <b>领域事件服务接口</b> */
public interface DomainEventService<T extends DomainEvent> extends IService<T>
{
	/** 默认发送批次大小 */
	int DEFAULT_SEND_BATCH_SIZE			= 100;
	/** 默认补偿批次大小 */
	int DEFAULT_COMPENSATE_BATCH_SIZE	= 200;
	/** 默认超时补偿时间（秒） */
	int DEFAULT_COMPENSATE_TIMEOUT		= 30;
	
	/** 标记一个领域事件为正在发送 */
	T markOneEventToSend();
	/** 标记一个领域事件为正在发送 */
	T markOneEventToSend(Long preId);
	
	/** 标记多个领域事件为正在发送 */
	List<T> markMultiEventToSend();
	/** 标记多个领域事件为正在发送 */
	List<T> markMultiEventToSend(Long preId);
	/** 标记多个领域事件为正在发送 */
	List<T> markMultiEventToSend(int batchSize);
	/** 标记多个领域事件为正在发送 */
	List<T> markMultiEventToSend(int batchSize, Long preId);
	
	/** 标记一个领域事件为发送成功 */
	boolean markOneEventSendSuccess(Long id);
	/** 标记多个领域事件为发送成功 */
	boolean markMultiEventSendSuccess(Collection<Long> ids);

	/** 标记一个领域事件为发送失败 */
	boolean markOneEventSendFail(Long id);
	/** 标记多个领域事件为发送失败 */
	boolean markMultiEventSendFail(Collection<Long> ids);

	/** 重设正在发送的领域事件状态为发送失败 */
	boolean resetSendingEventToSendFail();
	/** 重设正在发送的领域事件状态为发送失败 */
	boolean resetSendingEventToSendFail(int sendTimeout);
	/** 重设正在发送的领域事件状态为发送失败 */
	boolean resetSendingEventToSendFail(int sendTimeout, int batchSize);
}
