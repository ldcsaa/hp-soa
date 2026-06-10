
package io.github.hpsocket.soa.framework.web.annotation;

import java.lang.annotation.*;

/** <b>内网 HTTP 请求注解</b> */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SiteLocal
{

}
