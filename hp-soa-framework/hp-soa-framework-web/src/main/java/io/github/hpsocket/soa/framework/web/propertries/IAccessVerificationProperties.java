package io.github.hpsocket.soa.framework.web.propertries;

import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;

/** <b>HTTP 请求校验属性接口</b> */
public interface IAccessVerificationProperties
{
    /** 默认 HTTP 请求校验类型 */
    AccessVerification.Type getDefaultAccessPolicyEnum();
}
