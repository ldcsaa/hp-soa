package io.github.hpsocket.demo.mq.producer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.demo.mq.producer.entity.Order;
import io.github.hpsocket.demo.mq.producer.mapper.OrderMapper;
import io.github.hpsocket.demo.mq.producer.service.OrderService;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService
{
	@Autowired
	private DemoEventServiceImpl demoEventService;

	@Override
	public Order createOrder(Order order)
	{
		this.save(order);
		
		DemoEvent event = new DemoEvent(order.getId());
		
		return null;
	}
	
	
}
