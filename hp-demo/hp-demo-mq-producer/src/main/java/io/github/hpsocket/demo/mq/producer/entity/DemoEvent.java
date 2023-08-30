package io.github.hpsocket.demo.mq.producer.entity;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class DemoEvent extends DomainEvent
{
	private String bizId;
}
