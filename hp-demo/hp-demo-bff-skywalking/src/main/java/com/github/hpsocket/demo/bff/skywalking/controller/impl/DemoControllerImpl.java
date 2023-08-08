package com.github.hpsocket.demo.bff.skywalking.controller.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.github.hpsocket.demo.bff.skywalking.contract.req.DemoReuqest;
import com.github.hpsocket.demo.bff.skywalking.contract.resp.DemoResponse;
import com.github.hpsocket.demo.bff.skywalking.controller.DemoController;
import com.github.hpsocket.demo.infra.skywalking.service.DemoService;
import com.github.hpsocket.soa.framework.web.advice.RequestContext;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import com.github.hpsocket.soa.framework.web.model.Response;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.scheduling.annotation.Scheduled;
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
	@DubboReference
	DemoService demoService;

    /**
     * <p>@RequireLogin  LCB 登录检查注解</p>
     *
     * @param request Model 参数需要声明 @RequestBody 注解，如果需要校验参数则需要声明 @Valid 或 @Validated 注解
     * @return
     */
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request) {
        /** 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */
        
    	System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
    	
    	String name = demoService.sayHello(request.getName());

        DemoResponse resp = new DemoResponse();
        resp.setId(1001L);
        resp.setName(name);
        resp.setAge(request.getAge());
        resp.setToken("41784a5039322bbe55a8bf8ce29b9280");

		log.info("Trace: {}, {}, {}", TraceContext.traceId(), TraceContext.segmentId(), TraceContext.spanId());

        log.debug(resp.toString());

        Response<DemoResponse> response = new Response<>(resp);
        return response;
    }
    
    @Override
    @SentinelResource(value = "test")
    public Object test(@Valid DemoReuqest request)
    {
    	return "test";
    }
    
    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 5)
    void jobHandler()
    {
    	/* generate a new traceId before jobHandler() execute */
    	// ...
    	
    	// get current traceId
    	String traceId = TraceContext.traceId();
    	
    	log.info("{} JOB start ...", traceId);
    	
    	// other codes ...
    	// traceId transfer to other service ...
    	
    	log.info("{} JOB end !", traceId);
    }
}
