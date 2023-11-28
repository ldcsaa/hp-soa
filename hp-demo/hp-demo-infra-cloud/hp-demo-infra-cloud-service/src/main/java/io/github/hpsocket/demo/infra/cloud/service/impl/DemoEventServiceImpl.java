package io.github.hpsocket.demo.infra.cloud.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.demo.infra.cloud.entity.DemoEvent;
import io.github.hpsocket.demo.infra.cloud.mapper.DemoEventMapper;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.impl.DomainEventServiceImpl;

@Service
public class DemoEventServiceImpl extends DomainEventServiceImpl<DemoEventMapper, DemoEvent>
{

}
