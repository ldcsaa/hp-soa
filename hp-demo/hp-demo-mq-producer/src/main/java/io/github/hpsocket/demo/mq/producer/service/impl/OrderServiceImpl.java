package io.github.hpsocket.demo.mq.producer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.demo.mq.producer.config.AppConfig;
import io.github.hpsocket.demo.mq.producer.entity.DemoEvent;
import io.github.hpsocket.demo.mq.producer.entity.Order;
import io.github.hpsocket.demo.mq.producer.mapper.OrderMapper;
import io.github.hpsocket.demo.mq.producer.service.OrderService;
import io.github.hpsocket.soa.framework.leaf.service.GlobalIdService;
import io.github.hpsocket.soa.starter.rabbitmq.producer.service.DomainEventService;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService
{
	@Autowired
	private DomainEventService<DemoEvent> demoEventService;
	
	@Autowired
	private GlobalIdService globalIdService;

	@Override
	@Transactional
	public Order createOrder(Order order)
	{
		if(globalIdService.isSegmentIdSupported())
			System.out.println("段号ID：" + globalIdService.getSegmentId(GlobalIdService.TEST_SEGMENT_TAG));
		if(globalIdService.isSnowflakeIdSupported())
			System.out.println("雪花ID：" + globalIdService.getSnowflakeId());
		
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
