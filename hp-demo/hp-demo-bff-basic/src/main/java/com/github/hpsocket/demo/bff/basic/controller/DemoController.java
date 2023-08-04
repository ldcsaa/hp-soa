package com.github.hpsocket.demo.bff.basic.controller;

import com.github.hpsocket.demo.bff.basic.contract.req.DemoReuqest;
import com.github.hpsocket.demo.bff.basic.contract.resp.DemoResponse;
import com.github.hpsocket.soa.framework.web.model.Response;

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
    @PostMapping(value = "/queryUser")
    @Operation(summary = "查询用户", description = "通过姓名查询用户")
    Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request);

    @PostMapping(value = "/test")
    Object test(@RequestBody @Valid DemoReuqest request);
}
