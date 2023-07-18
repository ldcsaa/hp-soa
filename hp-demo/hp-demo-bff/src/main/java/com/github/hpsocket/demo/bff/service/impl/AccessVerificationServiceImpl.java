package com.github.hpsocket.demo.bff.service.impl;

import org.springframework.stereotype.Service;

import com.github.hpsocket.soa.framework.core.util.Pair;
import com.github.hpsocket.soa.framework.web.service.AccessVerificationService;

@Service
public class AccessVerificationServiceImpl implements AccessVerificationService
{
	@Override
	public Pair<Long, String> verifyUserByTokenAndGroupId(String token, Long groupId)
	{
		// TODO Auto-generated method stub
		return new Pair<Long, String>(123L, "OK");
	}
	
	@Override
	public Pair<Boolean, String> verifyRouteAuthorized(String route, String appCode, Long groupId, Long userId)
	{
		// TODO Auto-generated method stub
		return new Pair<Boolean, String>(Boolean.TRUE, "ok");
	}
	
	@Override
	public boolean verifyAppCode(String appCode)
	{
		// TODO Auto-generated method stub
		return true;
	}
}
