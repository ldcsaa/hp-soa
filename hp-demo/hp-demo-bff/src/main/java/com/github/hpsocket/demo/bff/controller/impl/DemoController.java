package com.github.hpsocket.demo.bff.controller.impl;

import com.github.hpsocket.demo.bff.contract.req.DemoReuqest;
import com.github.hpsocket.demo.bff.contract.resp.DemoResponse;
import com.github.hpsocket.demo.bff.controller.IDemoController;
import com.github.hpsocket.soa.framework.web.advice.RequestContext;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import com.github.hpsocket.soa.framework.web.model.Response;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author liangdongcheng
 *
 * <p>@NoLogin  LCB 登录检查注解 </p>
 * <p>@RestController  Spring MVC Rest Controller 注解 </p>
 */
@Slf4j
@AccessVerification(Type.NO_LOGIN)
@RestController
public class DemoController implements IDemoController
{
    //@Autowired
    //IDemoService demoService;
	
	@Value("${aaa.bbb}")
	Integer a;
	
	@Value("${xxx.yyy}")
	Integer x;
	

    /**
     * <p>@RequireLogin  LCB 登录检查注解</p>
     *
     * @param request Model 参数需要声明 @RequestBody 注解，如果需要校验参数则需要声明 @Valid 或 @Validated 注解
     * @return
     */
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
   // @FrequencyLimit(forbid = 3, type = Type.IP, value = 5)
    public Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request) {
        /** 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
        
        System.out.printf("aaa.bbb: %d, xxx.yyy: %d\n", a, x);

        DemoResponse resp = new DemoResponse();
        resp.setId(1001L);
        resp.setName(request.getName());
        resp.setAge(request.getAge());
        resp.setToken("41784a5039322bbe55a8bf8ce29b9280");

        log.debug(resp.toString());

        //log.debug(demoService.sayHello(request.getName()));

        Response<DemoResponse> response = new Response<>(resp);
        //response.setReturnType(Response.RT_LOGIN);

        return response;
    }
    
    @Override
    public Object test(@Valid DemoReuqest request)
    {
    	// TODO Auto-generated method stub
    	return "test";
    }
}
