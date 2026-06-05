package io.github.hpsocket.soa.framework.web.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/** <b>权限校验校验注解</b> */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuth
{
    /** 权限 */
    @AliasFor(attribute = "permission")
    String value();

    /** 权限 */
    @AliasFor(attribute = "value")
    String permission();
}
