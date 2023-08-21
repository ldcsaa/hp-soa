package io.github.hpsocket.soa.framework.web.propertries;

import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;

public interface IAccessVerificationProperties
{
	AccessVerification.Type getDefaultAccessPolicyEnum();
}
