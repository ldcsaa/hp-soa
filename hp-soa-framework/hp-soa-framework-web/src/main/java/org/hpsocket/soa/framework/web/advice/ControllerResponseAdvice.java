package org.hpsocket.soa.framework.web.advice;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.dubbo.rpc.RpcException;
import org.apache.logging.log4j.core.config.Order;
import org.hpsocket.soa.framework.core.exception.ServiceException;
import org.hpsocket.soa.framework.core.mdc.MdcAttr;
import org.hpsocket.soa.framework.core.mdc.MdcRunnable;
import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.core.util.Pair;
import org.hpsocket.soa.framework.web.model.RequestAttribute;
import org.hpsocket.soa.framework.web.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson2.JSONObject;
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanMap;

import static org.hpsocket.soa.framework.core.exception.ServiceException.*;
import static org.hpsocket.soa.framework.web.support.WebServerHelper.*;

@Slf4j
@Order(-10)
@RestControllerAdvice
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object>
{
	private static final ThreadPoolExecutor LOG_EXECUTOR = ControllerRequestAdvice.LOG_EXECUTOR;
	private static final Logger monitorLogger = LoggerFactory.getLogger(MONITOR_LOGGER_NAME);
	private static final UserAgentParser uaParser;
	
	static
	{
		try
		{
			uaParser = new UserAgentService().loadParser();
		}
		catch(IOException | ParseException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
	{
		return	RequestContext.getRequestAttribute() != null &&
				Response.class.isAssignableFrom(returnType.getNestedParameterType());
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response)
	{
		HttpServletRequest req	 = ((ServletServerHttpRequest)request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse)response).getServletResponse();
		
		if(body instanceof Response<?>)
		{
			Response<?> respBody = (Response<?>)body;
			
			if(respBody.getResultCode() == null)
				respBody.setResultCode(respBody.getStatusCode());
			
			checkToken(respBody, req, resp);

			respBody.calcCostTime(RequestContext.getTimestamp());
		}
		
		logResponse(body, returnType, req);
		
		RequestContext.removeRequestAttribute();

		return body;
	}

	@SuppressWarnings("unchecked")
	private void checkToken(Response<?> respBody, HttpServletRequest request, HttpServletResponse response)
	{
		Pair<Integer, String> tokenCookieAttr = null;
		
		Integer statusCode = respBody.getStatusCode();
		
		if(GeneralHelper.equals(statusCode, ServiceException.OK))
		{
			Integer rt = respBody.getRespType();
			
			if(rt != null)
			{
				if(rt == Response.RT_LOGIN)
				{
					Object result = respBody.getResult();
					
					if(result != null)
					{
						String token = null;
						
						if(result instanceof Map<?, ?>)
							token = ((Map<String, String>)result).get(HEADER_TOKEN);
						else
						{
							BeanMap map = BeanMap.create(result);
							token = (String)map.get(HEADER_TOKEN);
						}
						
						if(GeneralHelper.isStrNotEmpty(token))
							tokenCookieAttr = new Pair<Integer, String>(DEFAULT_COOKIE_MAX_AGE, token);
					}
				}
				else if(rt == Response.RT_LOGOUT)
					tokenCookieAttr = new Pair<Integer, String>(0, "");						
			}
		}
		else if(GeneralHelper.equals(statusCode, ServiceException.LOGIN_INVALID))
			tokenCookieAttr = new Pair<Integer, String>(0, "");
		
		if(tokenCookieAttr != null)
		{
			Cookie cookie = createCookie(request, HEADER_TOKEN, tokenCookieAttr.getSecond(), tokenCookieAttr.getFirst());
			response.addCookie(cookie);
		}		
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public Response<?> handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e)
	{
		BindingResult rs = e.getBindingResult();
		
		Map<String, List<String>> validationErrors = new LinkedHashMap<>();
		
		for(ObjectError obj : rs.getAllErrors())
		{
			if(obj instanceof FieldError)
			{
				FieldError field = (FieldError)obj;
				String name = field.getField();
				
				List<String> errs = validationErrors.get(name);
				
				if(errs == null)
				{
					errs = new LinkedList<>();
					validationErrors.put(name, errs);
				}
				
				errs.add(field.getDefaultMessage());
			}
		}
		
		if(validationErrors.isEmpty())
			validationErrors.put(rs.getObjectName(), Arrays.asList(rs.toString()));
		
		ServiceException se = wrapServiceException(PARAM_VERIFY_EXCEPTION, e);
		
		logServiceException(log, se.getMessage(), se);
		
		return new Response<>(se, validationErrors);
	}

	@ExceptionHandler({RpcException.class})
	public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, RpcException e)
	{
		Throwable real	= e;
		Throwable cause	= null;
		
		do
		{
			cause = real.getCause();
			
			if(cause == null)
				break;
			
			real = cause;
		} while(cause instanceof RpcException);
		
		ServiceException se = wrapServiceException(GENERAL_EXCEPTION, real);

		logServiceException(log, se.getMessage(), se);
		
		return new Response<>(se);
	}

	@ExceptionHandler({Exception.class})
	public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
	{
		ServiceException se = null;
		
		if(e instanceof NoHandlerFoundException)
			se = wrapServiceException(e.getMessage(), NOT_EXIST, e);
		else if(e instanceof HttpRequestMethodNotSupportedException)
			se = wrapServiceException(e.getMessage(), NOT_IMPLEMENTED, e);
		else
			se = wrapServiceException(GENERAL_EXCEPTION, e);

		logServiceException(log, se.getMessage(), se);
		
		return new Response<>(se);
	}

	private void logResponse(Object body, MethodParameter rt, HttpServletRequest req)
	{
		if(!log.isInfoEnabled())
			return;
		
		final RequestAttribute requestAttribute = RequestContext.getRequestAttribute();
		
		try
		{
			LOG_EXECUTOR.execute(new MdcRunnable()
			{
				@Override
				public void doRun()
				{
					log.info("[ RESPONSE: {} ] -> {}", requestAttribute.getRequestPath(), JSONObject.toJSONString(body, JSON_SERIAL_FEATURES_DEFAULT));
				}
			});
		}
		catch(Exception e)
		{
			log.error("async write response log fail", e);
		}
		
		asyncWriteMonitorLog(body, rt, req, requestAttribute);
	}
	
	private void asyncWriteMonitorLog(final Object body, final MethodParameter rt, final HttpServletRequest req, final RequestAttribute requestAttribute)
	{
		final String ua = getUserAgent(req);
		
		Runnable task = new MdcRunnable()
		{
			@Override
			protected void doRun()
			{
				JSONObject json		= JSONObject.from(requestAttribute.getBody());
				JSONObject jsonLog	= new JSONObject();
				
				if(json == null)
					json = new JSONObject();
			
				MdcAttr mdcAttr = getMdcAttr();

				jsonLog.put("logType", BFF_MONITOR_LOG_TYPE);
				jsonLog.put("appId", mdcAttr.getAppId());
				jsonLog.put("appName", mdcAttr.getAppName());
				jsonLog.put("serviceId", mdcAttr.getServiceId());
				jsonLog.put("serviceName", mdcAttr.getServiceName());
				jsonLog.put("serviceAddr", mdcAttr.getServiceAddr());
				jsonLog.put("organization", mdcAttr.getOrganization());
				jsonLog.put("owner", mdcAttr.getOwner());
				
				@SuppressWarnings("unchecked")
				Map<String, Object> reqAttr = (Map<String, Object>)BeanMap.create(requestAttribute);
				
				reqAttr.forEach((k, v) -> {
					if(v != null && !k.equals("body"))
						jsonLog.put((String)k, (String)v);
				});
				
				jsonLog.put("apiName", rt.getDeclaringClass().getSimpleName().concat("#").concat(rt.getMethod().getName()));
				jsonLog.put("request", json.toString());
				jsonLog.put("response", JSONObject.toJSONString(body, JSON_SERIAL_FEATURES_DEFAULT));
				
				if(body instanceof Response<?>)
				{
					Response<?> respBody = (Response<?>)body;
					
					jsonLog.put("resultCode", respBody.getResultCode());
					jsonLog.put("statusCode", respBody.getStatusCode());
					jsonLog.put("costTime", respBody.getCostTime());
					jsonLog.put("msg", GeneralHelper.equals(respBody.getStatusCode(), PARAM_VERIFY_ERROR) ? respBody.getMsg() + ": " + JSONObject.toJSONString(respBody.getValidationErrors()) : respBody.getMsg());
				}

				if(GeneralHelper.isStrNotEmpty(ua))
				{
					final Capabilities caps = uaParser.parse(ua);
					
					jsonLog.put("ua", ua);
					jsonLog.put("ua.browser", caps.getBrowser().concat(GeneralHelper.isStrNotEmpty(caps.getBrowserMajorVersion()) ? " " + caps.getBrowserMajorVersion() : ""));
					jsonLog.put("ua.browserType", caps.getBrowserType());
					jsonLog.put("ua.deviceType", caps.getDeviceType());
					jsonLog.put("ua.platform", caps.getPlatform().concat(GeneralHelper.isStrNotEmpty(caps.getPlatformVersion()) ? " " + caps.getPlatformVersion() : ""));
				}

				String msg = jsonLog.toJSONString();
				
				if(body instanceof Response<?>)
				{
					Response<?> respBody = (Response<?>)body;
					Integer statusCode	 = respBody.getStatusCode();
					
					if(GeneralHelper.equals(statusCode, OK))
						monitorLogger.info(msg);
					else if(GeneralHelper.equals(statusCode, GENERAL_ERROR))
						monitorLogger.error(msg);
					else
						monitorLogger.warn(msg);
				}
				else
				{
					monitorLogger.info(msg);
				}
			}
		};
				
		try
		{
			LOG_EXECUTOR.execute(task);
		}
		catch(Exception e)
		{
			log.error("async write monitor log fail", e);
		}
	}
	
}
