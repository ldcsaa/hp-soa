package io.github.hpsocket.demo.bff.nacos.service.impl;

import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import org.springframework.stereotype.Service;

import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.service.AccessVerificationService;

@Service
public class AccessVerificationServiceImpl implements AccessVerificationService
{
    @Override
    public Pair<Boolean, String> verifyAppCode(RequestAttribute reqAttr)
    {
        return new Pair<Boolean, String>(Boolean.TRUE, "ok");
    }

    @Override
    public Pair<Long, String> verifyUser(RequestAttribute reqAttr)
    {
        return new Pair<Long, String>(123L, "OK");
    }
    
    @Override
    public Pair<Boolean, String> verifyAuthorization(RequestAttribute reqAttr)
    {
        return new Pair<Boolean, String>(Boolean.TRUE, "ok");
    }
}
