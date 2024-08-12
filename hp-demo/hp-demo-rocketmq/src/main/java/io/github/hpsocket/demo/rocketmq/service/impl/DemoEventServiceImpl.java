package io.github.hpsocket.demo.rocketmq.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.demo.rocketmq.entity.DemoEvent;
import io.github.hpsocket.demo.rocketmq.mapper.DemoEventMapper;
import io.github.hpsocket.soa.starter.kafka.producer.service.impl.DomainEventServiceImpl;

@Service
public class DemoEventServiceImpl extends DomainEventServiceImpl<DemoEventMapper, DemoEvent>
{

}
