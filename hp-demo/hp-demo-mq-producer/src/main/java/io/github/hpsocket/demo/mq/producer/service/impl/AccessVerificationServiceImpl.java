package io.github.hpsocket.demo.mq.producer.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.service.AccessVerificationService;

@Service
public class AccessVerificationServiceImpl implements AccessVerificationService
{
	@Override
	public Pair<Long, String> verifyUserByTokenAndGroupId(String token, Long groupId)
	{
		return new Pair<Long, String>(123L, "OK");
	}
	
	@Override
	public Pair<Boolean, String> verifyRouteAuthorized(String route, String appCode, Long groupId, Long userId)
	{
		return new Pair<Boolean, String>(Boolean.TRUE, "ok");
	}
	
	@Override
	public boolean verifyAppCode(String appCode)
	{
		return true;
	}
}
