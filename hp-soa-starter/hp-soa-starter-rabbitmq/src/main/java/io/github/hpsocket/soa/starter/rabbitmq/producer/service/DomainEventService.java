package io.github.hpsocket.soa.starter.rabbitmq.producer.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;

public interface DomainEventService<T extends DomainEvent> extends IService<T>
{
	int DEFAULT_MARK_BATCH_SIZE	 = 100;
	int DEFAULT_RESET_BATCH_SIZE = 200;
	int DEFAULT_SEND_TIMEOUT	 = 30;
	
	T markOneEventToSend();
	T markOneEventToSend(Long preId);
	
	List<T> markMultiEventToSend();
	List<T> markMultiEventToSend(Long preId);
	List<T> markMultiEventToSend(int batchSize);
	List<T> markMultiEventToSend(int batchSize, Long preId);
	
	boolean markOneEventSendSuccess(Long id);
	boolean markMultiEventSendSuccess(Collection<Long> ids);

	boolean markOneEventSendFail(Long id);
	boolean markMultiEventSendFail(Collection<Long> ids);

	boolean resetSendingEventToSendFail();
	boolean resetSendingEventToSendFail(int sendTimeout);
	boolean resetSendingEventToSendFail(int sendTimeout, int batchSize);
}
