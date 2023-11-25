package io.github.hpsocket.demo.bff.cloud.controller;

import io.github.hpsocket.demo.bff.cloud.contract.req.QueryUserReuqest;
import io.github.hpsocket.demo.bff.cloud.contract.req.SaveUserReuqest;
import io.github.hpsocket.demo.bff.cloud.contract.resp.QueryUserResponse;
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
    @Operation(summary = "查询用户", description = "通过 ID 查询用户")
    Response<QueryUserResponse> queryUser(@RequestBody @Valid QueryUserReuqest request);

    @PostMapping(value = "/saveUser")
    @Operation(summary = "保存用户", description = "新增或更新用户")
    Response<Boolean> saveUser(@RequestBody @Valid SaveUserReuqest request);
}
