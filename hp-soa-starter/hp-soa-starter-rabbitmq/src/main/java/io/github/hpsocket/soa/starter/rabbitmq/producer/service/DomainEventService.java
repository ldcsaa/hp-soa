package io.github.hpsocket.soa.starter.rabbitmq.producer.service;

import java.util.List;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;

public interface DomainEventService<T extends DomainEvent>
{
	int DEFAULT_RESET_BATCH_SIZE = 200;
	int DEFAULT_SEND_TIMEOUT	 = 30;
	
	T markEventToSend();
	List<T> markEventToSend(int count);
	
	boolean markEventSendSuccess(long id);
	boolean markEventSendSuccess(List<Long> ids);

	boolean markEventSendFail(long id);
	boolean markEventSendFail(List<Long> ids);

	boolean resetSendingEventToSendFail();
	boolean resetSendingEventToSendFail(int sendTimeout);
	boolean resetSendingEventToSendFail(int sendTimeout, int batchSize);
}
