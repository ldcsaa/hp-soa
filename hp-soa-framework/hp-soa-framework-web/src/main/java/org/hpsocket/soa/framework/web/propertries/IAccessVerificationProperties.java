package org.hpsocket.soa.framework.web.propertries;

import org.hpsocket.soa.framework.web.annotation.AccessVerification;

public interface IAccessVerificationProperties
{
	AccessVerification.Type getDefaultAccessPolicyEnum();
}
