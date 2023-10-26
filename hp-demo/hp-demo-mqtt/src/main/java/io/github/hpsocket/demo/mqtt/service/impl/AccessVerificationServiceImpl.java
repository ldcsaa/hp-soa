package io.github.hpsocket.demo.mqtt.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.service.AccessVerificationService;

@Service
public class AccessVerificationServiceImpl implements AccessVerificationService
{
    @Override
    public Pair<Boolean, String> verifyAppCode(String appCode, String srcAppCode)
    {
        return new Pair<Boolean, String>(Boolean.TRUE, "ok");
    }

    @Override
    public Pair<Long, String> verifyUser(String token, Long groupId)
    {
        return new Pair<Long, String>(123L, "OK");
    }
    
    @Override
    public Pair<Boolean, String> verifyAuthorization(String route, String appCode, Long groupId, Long userId)
    {
        return new Pair<Boolean, String>(Boolean.TRUE, "ok");
    }
}
