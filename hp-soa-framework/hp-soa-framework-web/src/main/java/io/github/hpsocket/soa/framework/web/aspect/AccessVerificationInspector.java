package io.github.hpsocket.soa.framework.web.aspect;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.PreAuth;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.service.AccessVerificationService;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jboss.logging.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;
import static io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type.*;

/** <b>HTTP 请求校验拦截器</b><p>
 * 处理 {@linkplain AccessVerification} 注解
 */
@Aspect
@Order(0)
public class AccessVerificationInspector
{
    static final String POINTCUT_PATTERN    = """
                                                execution (public io.github.hpsocket.soa.framework.web.model.Response *.*(..)) &&
                                                (
                                                    @within(org.springframework.web.bind.annotation.RestController) ||
                                                    @within(org.springframework.stereotype.Controller)
                                                )
                                              """;

    private static final AspectHelper.AnnotationHolder<AccessVerification> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {};
    private static final AspectHelper.AnnotationHolder<PreAuth> PRE_AUTH_HOLDER = new AspectHelper.AnnotationHolder<>() {};

    @Pointcut(POINTCUT_PATTERN)
    protected void inspectMethod() {}
    
    private AccessVerification.Type defaultAccessPolicy;
    private AccessVerificationService accessVerificationService;

    private Map<Method, AccessVerification.Type> annotations = new ConcurrentHashMap<>();
    private Map<Method, String> permissions = new ConcurrentHashMap<>();
    
    public AccessVerificationInspector(AccessVerification.Type defaultAccessPolicy, AccessVerificationService accessVerificationService)
    {
        Assert.notNull(accessVerificationService, String.format("'%s' bean not found", AccessVerificationService.class.getSimpleName()));
        
        this.defaultAccessPolicy = defaultAccessPolicy;
        this.accessVerificationService = accessVerificationService;
    }

    @Around("inspectMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Response<?> resp = null;
        Method method    = AspectHelper.getMethod(joinPoint);
        AccessVerification.Type type = getInspectVerificationType(joinPoint, method);

        try
        {
            if(type != NO_CHECK)
            {
                RequestAttribute reqAttr      = RequestContext.getRequestAttribute();
                HttpServletResponse response  = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();

                if(reqAttr == null) {
                    response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
                    resp = new Response<Boolean>(BAD_REQUEST_EXCEPTION);
                    return resp;
                }

                resp = inspectApp(reqAttr);

                if(resp.getData() == null)
                {
                    response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
                    return resp;
                }

                if(type != NO_LOGIN)
                {
                    resp = inspectUser(reqAttr, type);

                    if(resp.getData() == null)
                    {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        return resp;
                    }

                    if(type == REQUIRE_AUTHORIZED)
                    {
                        checkPermission(joinPoint, method, reqAttr);
                        resp = inspectAuth(reqAttr);

                        if(resp.getData() == null)
                        {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            return resp;
                        }
                    }
                }
            }

            resp = (Response<?>)joinPoint.proceed();
            return resp;
        }
        finally
        {
            accessVerificationService.afterProcess(joinPoint, resp);
        }
    }

    private AccessVerification.Type getInspectVerificationType(ProceedingJoinPoint joinPoint, Method method)
    {
        AccessVerification.Type type = annotations.get(method);
        
        if(type == null)
        {
            AccessVerification annotation = ANNOTATION_HOLDER.findAnnotationByMethodOrClass(joinPoint);
            type = (annotation != null) ? annotation.value() : defaultAccessPolicy;

            annotations.putIfAbsent(method, type);
        }
        
        return type;
    }

    private Response<Boolean> inspectApp(RequestAttribute reqAttr)
    {
        Pair<Boolean, String> rs = accessVerificationService.verifyAppCode(reqAttr);
        
        if(!Boolean.TRUE.equals(rs.getFirst()))
        {
            String msg = rs.getSecond();
            
            if(GeneralHelper.isStrEmpty(msg))
                msg = APPCODE_CHECK_EXCEPTION.getMessage();

            return new Response<>(APPCODE_CHECK_ERROR, msg);
        }

        return new Response<>(Boolean.TRUE);
    }

    private Response<Long> inspectUser(RequestAttribute reqAttr, AccessVerification.Type type)
    {
        Pair<Long, String> rs = accessVerificationService.verifyUser(reqAttr);
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
                
                return new Response<>(AUTHEN_ERROR, msg);
            }
        }
        
        reqAttr.setUserId(userId);
        reqAttr.setUserName(rs.getSecond());
        MDC.put(MdcAttr.MDC_USER_ID_KEY, userId.toString());
        MDC.put(MdcAttr.MDC_USER_NAME_KEY, rs.getSecond());

        return new Response<>(userId);
    }

    private Response<Boolean> inspectAuth(RequestAttribute reqAttr)
    {
        Pair<Boolean, String> rs = accessVerificationService.verifyAuthorization(reqAttr);
        
        if(!Boolean.TRUE.equals(rs.getFirst()))
        {
            String msg = rs.getSecond();
            
            if(GeneralHelper.isStrEmpty(msg))
                msg = AUTHOR_EXCEPTION.getMessage();
            
            return new Response<>(AUTHOR_ERROR, msg);
        }

        return new Response<>(Boolean.TRUE);
    }

    private void checkPermission(ProceedingJoinPoint joinPoint, Method method, RequestAttribute reqAttr)
    {
        String permission = permissions.get(method);

        if(permission == null)
        {
            PreAuth annotation = PRE_AUTH_HOLDER.findAnnotationByMethodOrClass(joinPoint);
            permission = (annotation != null) ? annotation.value() : "";

            permissions.putIfAbsent(method, permission);
        }

        reqAttr.setPermission(permission);
    }
}
