package io.github.hpsocket.soa.framework.web.aspect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.MDC;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.service.AccessVerificationService;
import org.springframework.util.Assert;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;
import static io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type.*;

/** <b>HTTP 请求校验拦截器</b><br>
 * 处理 {@linkplain AccessVerification} 注解
 */
@Aspect
@Order(0)
public class AccessVerificationInspector
{
    static final String POINTCUT_PATTERN    = "execution (public io.github.hpsocket.soa.framework.web.model.Response *.*(..))"
                                            + " && @within(org.springframework.web.bind.annotation.RestController)";

    @Pointcut(POINTCUT_PATTERN)
    protected void inspectMethod() {}
    
    private AccessVerification.Type defaultAccessPolicy;
    private AccessVerificationService accessVerificationService;

    private Map<Method, AccessVerification.Type> annotations = new ConcurrentHashMap<>();
    
    public AccessVerificationInspector(AccessVerification.Type defaultAccessPolicy, AccessVerificationService accessVerificationService)
    {
        Assert.notNull(accessVerificationService, String.format("'%s' bean not found", AccessVerificationService.class.getSimpleName()));
        
        this.defaultAccessPolicy = defaultAccessPolicy;
        this.accessVerificationService = accessVerificationService;
    }

    @Around("inspectMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AccessVerification.Type type = getInspectVerificationType(signature.getMethod());
        
        if(type != NO_CHECK)
        {
            RequestAttribute reqAttr = RequestContext.getRequestAttribute();
            
            if(reqAttr == null)
                return new Response<Boolean>(BAD_REQUEST_EXCEPTION);
            
            Response<?> resp = inspectApp(reqAttr);
            
            if(resp.getResult() == null)
                return resp;
            
            if(type != NO_LOGIN)
            {
                resp = inspectUser(reqAttr, type);
                
                if(resp.getResult() == null)
                    return resp;
                
                if(type == REQUIRE_AUTHORIZED)
                {
                    resp = inspectRole(reqAttr);
                    
                    if(resp.getResult() == null)
                        return resp;
                }
            }
        }
        
        return joinPoint.proceed();
    }

    private AccessVerification.Type getInspectVerificationType(Method method)
    {
        AccessVerification.Type type = annotations.get(method);
        
        if(type == null)
        {
            type = getVerificationType(method);
            annotations.putIfAbsent(method, type);
        }
        
        return type;
    }

    private AccessVerification.Type getVerificationType(Method method)
    {
        AccessVerification annotation = method.getAnnotation(AccessVerification.class);
        
        if(annotation == null)
            annotation = method.getDeclaringClass().getAnnotation(AccessVerification.class);
        
        if(annotation == null)
            return defaultAccessPolicy;

        return annotation.value();
    }

    private Response<Boolean> inspectApp(RequestAttribute reqAttr)
    {
        String appCode    = reqAttr.getAppCode();
        String srcAppCode = reqAttr.getSrcAppCode();
        
        Pair<Boolean, String> rs = accessVerificationService.verifyAppCode(appCode, srcAppCode);
        
        if(!Boolean.TRUE.equals(rs.getFirst()))
        {
            String msg = rs.getSecond();
            
            if(GeneralHelper.isStrEmpty(msg))
                msg = APPCODE_CHECK_EXCEPTION.getMessage();
            
            return new Response<>(msg, APPCODE_CHECK_ERROR);
        }

        return new Response<>(Boolean.TRUE);
    }

    private Response<Long> inspectUser(RequestAttribute reqAttr, AccessVerification.Type type)
    {
        String token = reqAttr.getToken();
        
        if(GeneralHelper.isStrEmpty(token))
        {
            if(type == MAYBE_LOGIN)
                return new Response<>(RequestAttribute.GUEST_USER_ID);
            else
                return new Response<>(NOT_LOGGED_IN_EXCEPTION);
        }
        
        Pair<Long, String> rs = accessVerificationService.verifyUser(token, reqAttr.getGroupId());
        Long userId = rs.getFirst();
        
        if(userId == null)
        {
            if(type == MAYBE_LOGIN)
                return new Response<>(RequestAttribute.GUEST_USER_ID);
            else
            {
                String msg = rs.getSecond();
                
                if(GeneralHelper.isStrEmpty(msg))
                    msg = AUTHEN_EXCEPTION.getMessage();
                
                return new Response<>(msg, AUTHEN_ERROR);
            }
        }
        
        reqAttr.setUserId(userId);
        MDC.put(MdcAttr.MDC_USER_ID_KEY, userId.toString());
        
        return new Response<>(userId);
    }

    private Response<Boolean> inspectRole(RequestAttribute reqAttr)
    {
        Pair<Boolean, String> rs = accessVerificationService.verifyAuthorization(reqAttr.getRequestUri(), reqAttr.getAppCode(), reqAttr.getGroupId(), reqAttr.getUserId());
        
        if(!Boolean.TRUE.equals(rs.getFirst()))
        {
            String msg = rs.getSecond();
            
            if(GeneralHelper.isStrEmpty(msg))
                msg = AUTHOR_EXCEPTION.getMessage();
            
            return new Response<>(msg, AUTHOR_ERROR);
        }

        return new Response<>(Boolean.TRUE);
    }

}
