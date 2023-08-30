package io.github.hpsocket.soa.starter.rabbitmq.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;

public interface DomainEventMapper<T extends DomainEvent> extends BaseMapper<T>
{
	
}
