package io.github.hpsocket.demo.mq.producer.controller.impl;

import io.github.hpsocket.demo.infra.nacos.service.DemoService;
import io.github.hpsocket.demo.mq.producer.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.mq.producer.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.demo.mq.producer.controller.DemoController;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author liangdongcheng
 *
 * <p>@NoLogin  LCB 登录检查注解 </p>
 * <p>@RestController  Spring MVC Rest Controller 注解 </p>
 */
@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class DemoControllerImpl implements DemoController
{
    /**
     * <p>@RequireLogin  LCB 登录检查注解</p>
     *
     * @param request Model 参数需要声明 @RequestBody 注解，如果需要校验参数则需要声明 @Valid 或 @Validated 注解
     * @return
     */
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<DemoCreateOrderResponse> createOrder(@RequestBody @Valid DemoCreateOrderReuqest request) {
        /** 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */
        
    	System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
    	
    	String name = demoService.sayHello(request.getName());

        DemoCreateOrderResponse resp = new DemoCreateOrderResponse();
        resp.setId(1001L);
        resp.setName(name);
        resp.setAge(request.getAge());
        resp.setToken("41784a5039322bbe55a8bf8ce29b9280");

        log.debug(resp.toString());

        Response<DemoCreateOrderResponse> response = new Response<>(resp);
        return response;
    }
    
    @Override
    public Object test(@Valid DemoCreateOrderReuqest request)
    {
    	return "test";
    }
}
