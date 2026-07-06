package io.github.hpsocket.soa.framework.web.service;

import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.model.Response;
import org.aspectj.lang.ProceedingJoinPoint;

/** <b>HTTP 请求校验服务接口</b><br>
 * 所有需要执行 HTTP 请求校验的应用程序都必须实现该接口
 */
public interface AccessVerificationService
{
    /** 校验成功 */
    Pair<Boolean, String> VERIFY_SUCCESS  = new Pair<>(Boolean.TRUE);
    /** 校验失败 */
    Pair<Boolean, String> VERIFY_FAIL     = new Pair<>(Boolean.FALSE);
    /** 未登录用户 */
    Pair<Long, String> NOT_LOGGED_IN_USER = new Pair<>();
    
    /** 应用程序编号校验，成功：Pair(True, ?)，失败：Pair(False, msg) */
    default Pair<Boolean, String> verifyAppCode(RequestAttribute reqAttr)
    {
        return VERIFY_SUCCESS;
    }

    /** 用户身份校验，成功：Pair(userId, userName)，失败：Pair(null, msg) */
    default Pair<Long, String> verifyUser(RequestAttribute reqAttr)
    {
        return NOT_LOGGED_IN_USER;
    }

    /** 用户授权校验，成功：Pair(True, ?)，失败：Pair(False, msg) */
    default Pair<Boolean, String> verifyAuthorization(RequestAttribute reqAttr)
    {
        return VERIFY_SUCCESS;
    }

    /** 请求处理完成 */
    default void afterProcess(ProceedingJoinPoint joinPoint, Response<?> resp, Throwable error)
    {

    }

}
