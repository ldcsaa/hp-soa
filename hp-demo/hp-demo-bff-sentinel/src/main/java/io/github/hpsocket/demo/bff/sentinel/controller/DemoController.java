package io.github.hpsocket.demo.bff.sentinel.controller;

import io.github.hpsocket.demo.bff.sentinel.contract.req.DemoReuqest;
import io.github.hpsocket.demo.bff.sentinel.contract.resp.DemoResponse;
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
    @PostMapping(value = "/queryUser")
    @Operation(summary = "查询用户", description = "通过姓名查询用户")
    Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request);

    @PostMapping(value = "/test")
    Object test(@RequestBody @Valid DemoReuqest request);
}
