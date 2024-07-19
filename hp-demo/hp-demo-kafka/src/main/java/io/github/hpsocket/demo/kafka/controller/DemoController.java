package io.github.hpsocket.demo.kafka.controller;

import io.github.hpsocket.demo.kafka.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.kafka.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.soa.framework.web.model.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value = "/demo", method = {RequestMethod.POST})
@Tag(name = "示例Demo接口")
public interface DemoController
{
    @PostMapping(value = "/createOrder")
    @Operation(summary = "创建订单", description = "创建订单")
    Response<DemoCreateOrderResponse> createOrder(@RequestBody @Valid DemoCreateOrderReuqest request);

    @PostMapping(value = "/sendDirect")
    Response<DemoCreateOrderResponse> sendDirect(@RequestBody @Valid DemoCreateOrderReuqest request);

    @PostMapping(value = "/sendText")
    Response<Boolean> sendText(@Valid @NotBlank String text);
}
