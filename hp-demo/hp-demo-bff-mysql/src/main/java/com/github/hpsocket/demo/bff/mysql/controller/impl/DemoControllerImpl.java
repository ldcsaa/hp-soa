package com.github.hpsocket.demo.bff.mysql.controller.impl;

import com.github.hpsocket.demo.bff.mysql.contract.req.DemoReuqest;
import com.github.hpsocket.demo.bff.mysql.contract.resp.DemoResponse;
import com.github.hpsocket.demo.bff.mysql.controller.DemoController;
import com.github.hpsocket.demo.infra.mysql.bo.UserBo;
import com.github.hpsocket.demo.infra.mysql.service.DemoService;
import com.github.hpsocket.demo.infra.mysql.service.UserService;
import com.github.hpsocket.soa.framework.web.advice.RequestContext;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import com.github.hpsocket.soa.framework.web.model.Response;

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
	@DubboReference
	DemoService demoService;
	
	@DubboReference
	UserService userService;

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
    	
    	UserBo userBo = null;
    	String name = request.getName();
    	
    	if(name.equalsIgnoreCase("master"))
    		userBo = userService.getMasterUser();
    	else if(name.equalsIgnoreCase("slave"))
    		userBo = userService.getSlaveUser();
    	else if(name.equalsIgnoreCase("slave-1"))
    		userBo = userService.getSlave1User();
    	else if(name.equalsIgnoreCase("slave-2"))
    		userBo = userService.getSlave2User();
    	else
    		userBo = userService.getDefaultUser();
    	
    	Long userId = userBo != null ? userBo.getId() : null;
    	String userName = userBo != null ? userBo.getName() : "null";
    	Integer userAge = userBo != null ? userBo.getAge() : null;

        DemoResponse resp = new DemoResponse();
        resp.setId(userId);
        resp.setName(userName);
        resp.setAge(userAge);
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
