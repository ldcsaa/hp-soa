package io.github.hpsocket.demo.rocketmq.controller;

import io.github.hpsocket.demo.rocketmq.contract.req.CreateOrderReuqest;
import io.github.hpsocket.demo.rocketmq.contract.resp.CreateOrderResponse;
import io.github.hpsocket.soa.framework.web.model.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value = "/demo", method = {RequestMethod.POST})
@Tag(name = "示例Demo接口")
public interface DemoController
{
    @PostMapping(value = "/normal/createOrder")
    @Operation(summary = "创建订单", description = "创建订单")
    Response<CreateOrderResponse> normalCreateOrder(@RequestBody @Valid CreateOrderReuqest request);
    
    @PostMapping(value = "/fifo/createOrder")
    @Operation(summary = "创建订单", description = "创建订单")
    Response<CreateOrderResponse> fifoCreateOrder(@RequestBody @Valid CreateOrderReuqest request);
    
    @PostMapping(value = "/delay/createOrder")
    @Operation(summary = "创建订单", description = "创建订单")
    Response<CreateOrderResponse> delayCreateOrder(@RequestBody @Valid CreateOrderReuqest request);
    
    @PostMapping(value = "/trans/createOrder")
    @Operation(summary = "创建订单", description = "创建订单")
    Response<CreateOrderResponse> transCreateOrder(@RequestBody @Valid CreateOrderReuqest request);
    
}
