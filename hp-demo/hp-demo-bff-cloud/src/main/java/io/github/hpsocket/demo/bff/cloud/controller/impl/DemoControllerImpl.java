package io.github.hpsocket.demo.bff.cloud.controller.impl;

import io.github.hpsocket.demo.bff.cloud.client.UserServiceClient;
import io.github.hpsocket.demo.bff.cloud.contract.req.QueryUserReuqest;
import io.github.hpsocket.demo.bff.cloud.contract.req.SaveUserReuqest;
import io.github.hpsocket.demo.bff.cloud.contract.resp.QueryUserResponse;
import io.github.hpsocket.demo.bff.cloud.controller.DemoController;
import io.github.hpsocket.demo.bff.cloud.converter.UserConverter;
import io.github.hpsocket.demo.infra.cloud.bo.UserBo;
import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AccessVerification(Type.MAYBE_LOGIN)
public class DemoControllerImpl implements DemoController
{
    @Autowired
    UserServiceClient userServiceClient;
    
    @Autowired
    UserConverter userConverter;

    @Override
    public Response<QueryUserResponse> queryUser(@RequestBody @Valid QueryUserReuqest request)
    {
        /* 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
        
        UserBo userBo = userServiceClient.getUser(request.getId());
        
        QueryUserResponse resp = userConverter.toQueryUserResponse(userBo);
        
        if(resp != null)
            resp.setToken(IdGenerator.nextCompactUUID());

        return new Response<>(resp);
    }

    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<Boolean> saveUser(@RequestBody @Valid SaveUserReuqest request)
    {
        UserBo userBo = userConverter.fromSaveUserReuqest(request);
        boolean rs = userServiceClient.saveUser(userBo);

        return new Response<>(rs);
    }
    
}
