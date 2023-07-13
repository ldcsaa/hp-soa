package com.github.hpsocket.demo.bff.controller;

import com.github.hpsocket.demo.bff.contract.req.DemoReuqest;
import com.github.hpsocket.demo.bff.contract.resp.DemoResponse;
import com.lcb.soa.misc.server.spring.model.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


/**
 * @author liangdongcheng
 * <p>@RequestMapping Spring MVC Request Mapping 注解</p>
 * <p>@Api Swagger API 接口注解</p>
 */
@RequestMapping(value = "/demo", method = {RequestMethod.POST})
@Api(tags = {"示例Demo接口"})
public interface IDemoController {
    /**
     * <p>@PostMapping Spring MVC Post Mapping 注解</p>
     * <p>@ApiOperation Swagger API 接口方法注解 </p>
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/queryUser")
    @ApiOperation(value = "查询用户", notes = "通过姓名查询用户")
    Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request);

}
