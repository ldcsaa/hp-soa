package io.github.hpsocket.soa.starter.rabbitmq.producer.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;

public interface DomainEventService<T extends DomainEvent> extends IService<T>
{
	int DEFAULT_RESET_BATCH_SIZE = 200;
	int DEFAULT_SEND_TIMEOUT	 = 30;
	
	T markEventToSend();
	List<T> markEventToSend(int count);
	
	boolean markEventSendSuccess(long id);
	boolean markEventSendSuccess(Collection<Long> ids);

	boolean markEventSendFail(long id);
	boolean markEventSendFail(Collection<Long> ids);

	boolean resetSendingEventToSendFail();
	boolean resetSendingEventToSendFail(int sendTimeout);
	boolean resetSendingEventToSendFail(int sendTimeout, int batchSize);
}
