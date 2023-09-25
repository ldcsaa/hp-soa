package io.github.hpsocket.demo.bff.basic.controller.impl;

import io.github.hpsocket.demo.bff.basic.contract.req.DemoReuqest;
import io.github.hpsocket.demo.bff.basic.contract.resp.DemoResponse;
import io.github.hpsocket.demo.bff.basic.controller.DemoController;
import io.github.hpsocket.demo.infra.basic.service.DemoService;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class DemoControllerImpl implements DemoController
{
    @DubboReference
    DemoService demoService;

    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request)
    {
        /* 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */    
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
        
        String name = demoService.sayHello(request.getName());

        DemoResponse resp = new DemoResponse();
        resp.setId(1001L);
        resp.setName(name);
        resp.setAge(request.getAge());
        resp.setToken("41784a5039322bbe55a8bf8ce29b9280");

        log.debug(resp.toString());

        Response<DemoResponse> response = new Response<>(resp);
        return response;
    }
    
    @Override
    public Object test(@Valid DemoReuqest request)
    {
        return "test";
    }
}
