package io.github.hpsocket.demo.kafka.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.demo.kafka.entity.DemoEvent;
import io.github.hpsocket.demo.kafka.mapper.DemoEventMapper;
import io.github.hpsocket.soa.starter.kafka.producer.service.impl.DomainEventServiceImpl;

@Service
public class DemoEventServiceImpl extends DomainEventServiceImpl<DemoEventMapper, DemoEvent>
{

}
