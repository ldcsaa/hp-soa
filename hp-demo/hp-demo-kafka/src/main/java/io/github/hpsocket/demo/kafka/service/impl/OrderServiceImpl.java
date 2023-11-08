package io.github.hpsocket.demo.kafka.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.demo.kafka.config.AppConfig;
import io.github.hpsocket.demo.kafka.entity.DemoEvent;
import io.github.hpsocket.demo.kafka.entity.Order;
import io.github.hpsocket.demo.kafka.mapper.OrderMapper;
import io.github.hpsocket.demo.kafka.service.OrderService;
import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.starter.kafka.producer.service.DomainEventService;

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
        JSONObject headers = new JSONObject();
        headers.put("x-customer-trace-id", IdGenerator.nextIdStr());
        
        DemoEvent event = new DemoEvent(order.getId(), order.getRegionId());
        
        event.setDomainName(AppConfig.DOMAIN_NAME);
        event.setEventName(AppConfig.CREATE_ORDER_EVENT_NAME);
        event.setTopic(AppConfig.TOPICS[event.getRegionId()]);
        //event.setTopicPartition(null);
        //event.setMsgTimestamp(System.currentTimeMillis());
        event.setMsgKey(IdGenerator.nextIdStr());
        event.setMsgHeaders(headers.toString());
        event.setMsg(JSONObject.toJSONString(order));
        
        demoEventService.save(event);
    }

}
