package io.github.hpsocket.demo.mq.producer.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.demo.mq.producer.mapper.DemoEventMapper;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.impl.DomainEventServiceImpl;

@Service
public class DemoEventServiceImpl extends DomainEventServiceImpl<DemoEventMapper, DemoEvent>
{

}
