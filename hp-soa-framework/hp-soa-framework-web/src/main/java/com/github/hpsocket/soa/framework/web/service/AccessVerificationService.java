package com.github.hpsocket.soa.framework.web.service;

import com.github.hpsocket.soa.framework.core.util.Pair;

public interface AccessVerificationService
{
	boolean verifyAppCode(String appCode);
	Pair<Long, String> verifyUserByTokenAndGroupId(String token, Long groupId);
	Pair<Boolean, String> verifyRouteAuthorized(String route, String appCode, Long groupId, Long userId);

}
