package io.github.hpsocket.soa.framework.web.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.Assert;

import io.github.hpsocket.soa.framework.web.annotation.ReadOnlyGuard;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

/** <b>只读保护请求拦截器</b><p>
 * 处理 {@linkplain ReadOnlyGuard} 注解
 */
@Aspect
@Order(Integer.MIN_VALUE)
public class ReadOnlyGuardInspector
{
    private static final String POINTCUT_PATTERN = "@annotation(io.github.hpsocket.soa.framework.web.annotation.ReadOnlyGuard) || @within(io.github.hpsocket.soa.framework.web.annotation.ReadOnlyGuard)";
    
    private static final AspectHelper.AnnotationHolder<ReadOnlyGuard> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};


    @Pointcut(POINTCUT_PATTERN)
    protected void beforeMethod() {}
    
    @Before(value = "beforeMethod()")
    public void verifyReadOnly(JoinPoint point)
    {
        if(!AppConfigHolder.isReadOnly())
            return;
        
        ReadOnlyGuard guard = ANNOTATION_HOLDER.findAnnotationByMethodOrClass(point);
        Assert.notNull(guard, "@ReadOnlyGuard annotation not found");
        
        if(!guard.enabled())
            return;
        
        WebServerHelper.assertAppIsNotReadOnly(guard.desc());
    }

}
