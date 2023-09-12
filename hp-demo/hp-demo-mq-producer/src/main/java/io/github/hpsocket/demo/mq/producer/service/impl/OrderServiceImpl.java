package io.github.hpsocket.demo.mq.producer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.demo.mq.producer.config.AppConfig;
import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.demo.mq.producer.entity.Order;
import io.github.hpsocket.demo.mq.producer.mapper.OrderMapper;
import io.github.hpsocket.demo.mq.producer.service.OrderService;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService
{
	@Autowired
	private DomainEventService<DemoEvent> demoEventService;

	@Override
	public Order createOrder(Order order)
	{
		save(order);
		raiseCreateOrderEvent(order);
		
		return order;
	}

	private void raiseCreateOrderEvent(Order order)
	{
		DemoEvent event = new DemoEvent(order.getId(), order.getRegionId());
		
		event.setDomainName(AppConfig.DOMAIN_NAME);
		event.setEventName(AppConfig.CREATE_ORDER_EVENT_NAME);
		event.setExchange(AppConfig.REGION_EXCHANGES[event.getRegionId()]);
		event.setRoutingKey(AppConfig.CREATE_ORDER_ROUTING_KEY);
		event.setMsg(JSONObject.toJSONString(order));
		
		demoEventService.save(event);
	}

}
