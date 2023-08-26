package io.github.hpsocket.soa.starter.rabbitmq.producer.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;

public class DomainEventServiceImpl<M extends BaseMapper<T>, T extends DomainEvent> extends ServiceImpl<M, T> implements DomainEventService<T>
{

}
