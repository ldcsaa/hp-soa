package com.github.hpsocket.demo.bff.controller.impl;

import com.github.hpsocket.demo.bff.contract.req.DemoReuqest;
import com.github.hpsocket.demo.bff.contract.resp.DemoResponse;
import com.github.hpsocket.demo.bff.controller.IDemoController;
import com.lcb.soa.demo.service.IDemoService;
import com.lcb.soa.misc.aspect.frequency.FrequencyLimit;
import com.lcb.soa.misc.aspect.frequency.FrequencyLimit.Type;
import com.lcb.soa.misc.biz.hapi.annotation.NoLogin;
import com.lcb.soa.misc.biz.hapi.annotation.RequireLogin;
import com.lcb.soa.misc.server.spring.advice.RequestContext;
import com.lcb.soa.misc.server.spring.model.Response;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @author liangdongcheng
 *
 * <p>@NoLogin  LCB 登录检查注解 </p>
 * <p>@RestController  Spring MVC Rest Controller 注解 </p>
 */
@Slf4j
@NoLogin
@RestController
public class DemoController implements IDemoController {
    @Autowired
    IDemoService demoService;

    /**
     * <p>@RequireLogin  LCB 登录检查注解</p>
     *
     * @param request Model 参数需要声明 @RequestBody 注解，如果需要校验参数则需要声明 @Valid 或 @Validated 注解
     * @return
     */
    @Override
    @RequireLogin
    @FrequencyLimit(forbid = 3, type = Type.IP, value = 5)
    public Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request) {
        /** 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());

        DemoResponse resp = new DemoResponse();
        resp.setId(1001L);
        resp.setName(request.getName());
        resp.setAge(request.getAge());
        resp.setToken("41784a5039322bbe55a8bf8ce29b9280");

        log.debug(resp.toString());

        log.debug(demoService.sayHello(request.getName()));

        Response<DemoResponse> response = new Response<>(resp);
        //response.setReturnType(Response.RT_LOGIN);

        return response;
    }
}
