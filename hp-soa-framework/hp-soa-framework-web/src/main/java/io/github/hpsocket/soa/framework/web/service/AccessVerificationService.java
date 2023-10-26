package io.github.hpsocket.soa.framework.web.service;

import io.github.hpsocket.soa.framework.core.util.Pair;

/** <b>HTTP 请求校验服务接口</b><br>
 * 所有需要执行 HTTP 请求校验的应用程序都必须实现该接口
 */
public interface AccessVerificationService
{
    /** 应用程序编号校验，成功：Pair(True, ?)，失败：Pair(False, ?) */
    Pair<Boolean, String> verifyAppCode(String appCode, String srcAppCode);
    /** 用户身份校验，成功：Pair(userId, ?)，失败：Pair(null, ?) */
    Pair<Long, String> verifyUser(String token, Long groupId);
    /** 用户授权校验，成功：Pair(True, ?)，失败：Pair(False, ?) */
    Pair<Boolean, String> verifyAuthorization(String route, String appCode, Long groupId, Long userId);

}
