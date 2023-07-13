package com.github.hpsocket.soa.framework.web.aspect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.core.util.Pair;
import com.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import com.github.hpsocket.soa.framework.web.advice.RequestContext;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import com.github.hpsocket.soa.framework.web.model.RequestAttribute;
import com.github.hpsocket.soa.framework.web.model.Response;
import com.github.hpsocket.soa.framework.web.service.AccessVerificationService;
import org.springframework.util.Assert;

import static com.github.hpsocket.soa.framework.core.exception.ServiceException.*;
import static com.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type.*;

@Aspect
@Order(0)
public class AccessVerificationInspector
{
	private static final String POINTCUT_PATTERN = ControllerRequestAdvice.POINTCUT_PATTERN;

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
		String appCode = reqAttr.getAppCode();
		
		if(GeneralHelper.isStrEmpty(appCode))
			return new Response<>("缺少参数：appCode", VALIDATE_ERROR);
		
		if(!accessVerificationService.verifyAppCode(appCode))
			return new Response<>(APPCODE_NOT_EXIST_EXCEPTION);
		
		return new Response<>(Boolean.TRUE);
	}

	private Response<Long> inspectUser(RequestAttribute reqAttr, AccessVerification.Type type)
	{
		String token = reqAttr.getToken();
		
		if(GeneralHelper.isStrEmpty(token))
		{
			if(type == MAYBE_LOGIN)
				return new Response<>(0L);
			else
				return new Response<>(NOT_LOGGED_IN_EXCEPTION);
		}
		
		Pair<Long, String> rs = accessVerificationService.verifyUserByTokenAndGroupId(token, reqAttr.getGroupId());
		Long userId = rs.getFirst();
		
		if(GeneralHelper.isNullOrZero(userId))
		{
			if(type == MAYBE_LOGIN)
				return new Response<>(0L);
			else
			{
				String msg = rs.getSecond();
				
				if(GeneralHelper.isStrEmpty(msg))
					msg = AUTHEN_EXCEPTION.getMessage();
				
				return new Response<>(msg, AUTHEN_ERROR);
			}
		}
		
		reqAttr.setUserId(userId);
		
		return new Response<>(userId);
	}

	private Response<Boolean> inspectRole(RequestAttribute reqAttr)
	{
		if(reqAttr.getGroupId() == null)
			return new Response<>("组织ID为空", AUTHOR_ERROR);
		
		Pair<Boolean, String> rs = accessVerificationService.verifyRouteAuthorized(reqAttr.getRequestUri(), reqAttr.getAppCode(), reqAttr.getGroupId(), reqAttr.getUserId());
		
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
