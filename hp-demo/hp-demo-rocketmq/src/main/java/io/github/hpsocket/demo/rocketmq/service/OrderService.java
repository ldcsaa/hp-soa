package io.github.hpsocket.demo.rocketmq.service;

import io.github.hpsocket.demo.rocketmq.entity.Order;

public interface OrderService
{
    Order createOrder(Order order);
}
