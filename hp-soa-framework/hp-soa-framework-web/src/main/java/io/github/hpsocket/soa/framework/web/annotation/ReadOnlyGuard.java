
package io.github.hpsocket.soa.framework.web.annotation;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/** <b>只读保护注解</b><p>
 * 只读应用程序（${hp.soa.web.app.read-only=true}），当调用声明了 {@linkplain ReadOnlyGuard} 注解的类或方式时，会抛出 {@linkplain ServiceException#READ_ONLY_ERROR} 异常
 * 
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ReadOnlyGuard
{
    /** 是否开启只读保护（默认：true） */
    @AliasFor(attribute = "enabled")
    boolean value() default true;
    
    /** 是否开启只读保护（默认：true） */
    @AliasFor(attribute = "value")
    boolean enabled() default true;
    
    /** 自定义 {@linkplain ServiceException#READ_ONLY_ERROR} 异常描述 */
    String desc() default "";
}
