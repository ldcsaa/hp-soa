package io.github.hpsocket.demo.mq.producer.controller;

import io.github.hpsocket.demo.mq.producer.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.mq.producer.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.soa.framework.web.model.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author liangdongcheng
 * <p>@RequestMapping Spring MVC Request Mapping 注解</p>
 * <p>@Api Swagger API 接口注解</p>
 */
@RequestMapping(value = "/demo", method = {RequestMethod.POST})
@Tag(name = "示例Demo接口")
public interface DemoController {
    /**
     * <p>@PostMapping Spring MVC Post Mapping 注解</p>
     * <p>@ApiOperation Swagger API 接口方法注解 </p>
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/createOrder")
    @Operation(summary = "创建订单", description = "创建订单")
    Response<DemoCreateOrderResponse> createOrder(@RequestBody @Valid DemoCreateOrderReuqest request);

    @PostMapping(value = "/test")
    Object test(@RequestBody @Valid DemoCreateOrderReuqest request);
}
