package io.github.hpsocket.demo.mq.producer.controller.impl;

import io.github.hpsocket.demo.mq.producer.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.mq.producer.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.demo.mq.producer.controller.DemoController;
import io.github.hpsocket.demo.mq.producer.converter.OrderConverter;
import io.github.hpsocket.demo.mq.producer.entity.Order;
import io.github.hpsocket.demo.mq.producer.sender.StreamSender;
import io.github.hpsocket.demo.mq.producer.service.OrderService;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class DemoControllerImpl implements DemoController
{
    @Autowired
    private OrderService orderService;
    
    @Autowired
    StreamSender streamSender;
    
    @Autowired
    private OrderConverter orderConverter;
    
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<DemoCreateOrderResponse> createOrder(@RequestBody @Valid DemoCreateOrderReuqest request)
    {
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
        
        Order order = orderService.createOrder(orderConverter.fromRequest(request));
        DemoCreateOrderResponse resp = orderConverter.toResponse(order);
        
        log.debug(resp.toString());

        Response<DemoCreateOrderResponse> response = new Response<>(resp);
        return response;
    }
    
    @Override
    public Response<DemoCreateOrderResponse> sendStream(@Valid DemoCreateOrderReuqest request)
    {
        Order order = streamSender.sendOrder(orderConverter.fromRequest(request));
        DemoCreateOrderResponse resp = orderConverter.toResponse(order);
        
        log.debug(resp.toString());

        Response<DemoCreateOrderResponse> response = new Response<>(resp);
        return response;
    }
}
