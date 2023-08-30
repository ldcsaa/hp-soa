package io.github.hpsocket.demo.mq.producer.service.impl;

import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.demo.mq.producer.mapper.DemoEventMapper;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.impl.DomainEventServiceImpl;
import io.swagger.v3.oas.annotations.servers.Server;

@Server
public class DemoEventServiceImpl extends DomainEventServiceImpl<DemoEventMapper, DemoEvent>
{

}
