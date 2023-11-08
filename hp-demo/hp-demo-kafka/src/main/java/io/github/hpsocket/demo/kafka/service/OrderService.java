package io.github.hpsocket.demo.kafka.service;

import io.github.hpsocket.demo.kafka.entity.Order;

public interface OrderService
{
    Order createOrder(Order order);
}
