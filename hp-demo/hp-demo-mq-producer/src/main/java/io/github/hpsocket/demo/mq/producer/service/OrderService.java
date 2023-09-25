package io.github.hpsocket.demo.mq.producer.service;

import io.github.hpsocket.demo.mq.producer.entity.Order;

public interface OrderService
{
    Order createOrder(Order order);
}
