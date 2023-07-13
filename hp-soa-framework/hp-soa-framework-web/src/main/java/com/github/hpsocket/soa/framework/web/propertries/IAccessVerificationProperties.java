package com.github.hpsocket.soa.framework.web.propertries;

import com.github.hpsocket.soa.framework.web.annotation.AccessVerification;

public interface IAccessVerificationProperties
{
	AccessVerification.Type getDefaultAccessPolicyEnum();
}
