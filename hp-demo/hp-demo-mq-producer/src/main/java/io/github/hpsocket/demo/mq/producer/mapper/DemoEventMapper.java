package io.github.hpsocket.demo.mq.producer.mapper;

import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.soa.starter.rabbitmq.producer.mapper.DomainEventMapper;

public interface DemoEventMapper extends DomainEventMapper<DemoEvent>
{

}
