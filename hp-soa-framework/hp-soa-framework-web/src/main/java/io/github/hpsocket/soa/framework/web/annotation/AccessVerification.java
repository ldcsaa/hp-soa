package io.github.hpsocket.soa.framework.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** <b>HTTP 请求校验注解</b> */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AccessVerification
{
    /** <b>HTTP 请求校验类型</b> */
    public static enum Type
    {
        /** 不作任何校验 */
        NO_CHECK,
        /** 不校验登录（只校验 appCode） */
        NO_LOGIN,
        /** 不强制校验登录（只校验 appCode），如果已登录则加载用户信息 */
        MAYBE_LOGIN,
        /** 校验登录 */
        REQUIRE_LOGIN,
        /** 校验授权 */
        REQUIRE_AUTHORIZED
    }
    
    /** 校验类型：（默认：{@linkplain Type#MAYBE_LOGIN MAYBE_LOGIN}） */
    Type value() default Type.MAYBE_LOGIN;
}
