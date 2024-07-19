package io.github.hpsocket.demo.mq.producer.controller;

import io.github.hpsocket.demo.mq.producer.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.mq.producer.contract.resp.DemoCreateOrderResponse;
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

    @PostMapping(value = "/sendStream")
    Response<DemoCreateOrderResponse> sendStream(@RequestBody @Valid DemoCreateOrderReuqest request);

    @PostMapping(value = "/sendText")
    Response<Boolean> sendText(@Valid @NotBlank String text);

    @PostMapping(value = "/sendStreamText")
    Response<Boolean> sendStreamText(@Valid @NotBlank String text);
}
