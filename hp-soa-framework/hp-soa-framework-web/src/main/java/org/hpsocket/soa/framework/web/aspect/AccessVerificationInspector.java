package org.hpsocket.soa.framework.web.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hpsocket.soa.framework.core.util.Pair;
import org.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import org.hpsocket.soa.framework.web.annotation.AccessVerification;
import org.hpsocket.soa.framework.web.model.RequestAttribute;
import org.hpsocket.soa.framework.web.model.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static org.hpsocket.soa.framework.core.exception.ServiceException.*;
import static org.hpsocket.soa.framework.web.annotation.AccessVerification.Type.*;

@Aspect
@Order(0)
public class AccessVerificationInspector implements ApplicationContextAware
{
	private static final Set<AccessVerification.Type> INSPECTED_ANNOTATIOS = new HashSet<>(Arrays.asList(NO_CHECK, NO_LOGIN, MAYBE_LOGIN, REQUIRE_LOGIN, REQUIRE_AUTHORIZED));
	private static final String POINTCUT_PATTERN = ControllerRequestAdvice.POINTCUT_PATTERN;

	@Pointcut(POINTCUT_PATTERN)
	protected void inspectMethod() {}
	
	@Value("${hapi.config.inspect.appAndUser.defaultPolicy:NoLogin}")
	private String defaultPolicy;
	@Value("${hapi.config.inspect.appAndUser.tokenAliasSupported:true}")
	private boolean tokenAliasSupported;
	
	private Class<? extends Annotation> clsDefaultPolicy;
	
	private IApplicationService applicationService;
	private IRoleAuthService roleAuthService;

	private Map<Method, Class<? extends Annotation>> annotations = new ConcurrentHashMap<>();
	
	@PostConstruct
	public void postConstruct()
	{
		for(Class<? extends Annotation> clazz : INSPECTED_ANNOTATIOS)
		{
			if(clazz.getSimpleName().equalsIgnoreCase(defaultPolicy))
			{
				clsDefaultPolicy = clazz;
				break;
			}
		}
		
		if(clsDefaultPolicy == null)
			clsDefaultPolicy = NoLogin.class;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		applicationService = applicationContext.getBean(IApplicationService.class);
		roleAuthService = applicationContext.getBean(IRoleAuthService.class);
	}

	@Around("inspectMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
	{
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Class<? extends Annotation> clazz = getInspectAnnotationClass(signature.getMethod());
		
		if(NoCheck.class.equals(clazz))
			return joinPoint.proceed();
		
		RequestAttribute reqAttr = RequestContext.getRequestAttribute();
		
		if(reqAttr == null)
			return new Response<Boolean>(BAD_REQUEST_EXCEPTION);
		
		Response<?> resp = inspectApp(reqAttr);
		
		if(resp.getResult() == null)
			return resp;
		
		if(NoLogin.class.equals(clazz))
			return joinPoint.proceed();
		
		resp = inspectUser(reqAttr, clazz);
		
		if(resp.getResult() == null)
			return resp;
		
		if(!RequireAuthorized.class.equals(clazz))
			return joinPoint.proceed();

		resp = inspectRole(reqAttr);
		
		if(resp.getResult() == null)
			return resp;	
		
		return joinPoint.proceed();
	}

	private Class<? extends Annotation> getInspectAnnotationClass(Method method)
	{
		Class<? extends Annotation> clazz = annotations.get(method);
		
		if(clazz == null)
		{
			clazz = getMethodAnnotation(method);
			annotations.putIfAbsent(method, clazz);
		}
		
		return clazz;
	}

	private Class<? extends Annotation> getMethodAnnotation(Method method)
	{
		Annotation[] annotations = method.getAnnotations();
		Annotation annotation = checkAnnotations(annotations);
		
		if(annotation == null)
		{
			annotations = method.getDeclaringClass().getAnnotations();
			annotation = checkAnnotations(annotations);
		}
		
		if(annotation == null)
			return clsDefaultPolicy;

		return annotation.annotationType();
	}

	private Annotation checkAnnotations(Annotation[] annotations)
	{
		Annotation annotation = null;
		
		for(int i = 0; i < annotations.length; i++)
		{
			if(INSPECTED_ANNOTATIOS.contains(annotations[i].annotationType()))
			{
				annotation = annotations[i];
				break;
			}
		}
		
		return annotation;
	}

	private Response<Integer> inspectApp(RequestAttribute reqAttr)
	{
		String appCode = reqAttr.getAppCode();
		
		if(GeneralHelper.isStrEmpty(appCode))
			return new Response<>("appCode is null or empty", APPCODE_ERROR);
		
		Application app = applicationService.getByAppCode(appCode);
		
		if(app == null)
			return new Response<>("invalid appCode", APPCODE_ERROR);
		
		return new Response<>(app.getId());
	}

	private Response<Long> inspectUser(RequestAttribute reqAttr, Class<? extends Annotation> clazz)
	{
		String token = null;
		String internalToken = null;
		
		if(!tokenAliasSupported || GeneralHelper.isStrEmpty(reqAttr.getTokenAlias()))
		{
			token = reqAttr.getToken();
			internalToken = token;
		}
		else
		{
			token = reqAttr.getTokenAlias();
			internalToken = reqAttr.getToken();
		}
		
		if(GeneralHelper.isStrEmpty(token))
		{
			if(clazz.equals(MaybeLogin.class))
				return new Response<>(0L);
			else
				return new Response<>(NOT_LOGGED_IN_EXCEPTION);
		}
		
		Pair<Long, Long> userIdPair = parseUser(token, internalToken);
		
		if(userIdPair == null || userIdPair.getFirst() == null)
		{
			if(clazz.equals(MaybeLogin.class))
				return new Response<>(0L);
			else
				return new Response<>(LOGIN_INVALID_EXCEPTION);
		}
		
		reqAttr.setUserId(userIdPair.getFirst());
		reqAttr.setInternalUserId(userIdPair.getSecond());
		
		return verifyUser(reqAttr);
	}
	
	private Pair<Long, Long> parseUser(String token, String internalToken)
	{
		return null;
	}
	
	private Response<Long> verifyUser(RequestAttribute reqAttr)
	{
		return null;
	}
	


	private Response<Boolean> inspectRole(RequestAttribute reqAttr)
	{
		if(reqAttr.getGroupId() == null || reqAttr.getGroupType() == null)
			return new Response<>("组织类型或组织ID为空", AUTHOR_ERROR);
		
		UserRoleCheckByGroupRequest roleReq = new UserRoleCheckByGroupRequest();
		
		roleReq.setRoute(reqAttr.getRequestUri());
		roleReq.setUserId(reqAttr.getInternalUserId());
		roleReq.setGroupId(reqAttr.getGroupId());
		roleReq.setGroupType(reqAttr.getGroupType());
		
		if(!roleAuthService.checkByGroup(roleReq, reqAttr.getAppCode()))
			return new Response<>(AUTHOR_EXCEPTION);

		return new Response<>(Boolean.TRUE);
	}

}
