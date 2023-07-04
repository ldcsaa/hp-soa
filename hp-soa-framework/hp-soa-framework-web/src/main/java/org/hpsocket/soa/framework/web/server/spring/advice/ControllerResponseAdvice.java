package org.hpsocket.soa.framework.web.server.spring.advice;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.config.Order;
import org.hpsocket.soa.framework.web.server.RequestContext;
import org.hpsocket.soa.framework.web.server.model.RequestAttribute;
import org.hpsocket.soa.framework.web.server.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
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

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSONObject;
import com.lcb.soa.misc.common.exception.ServiceException;
import com.lcb.soa.misc.common.thread.MdcTracingThreadPoolExecutor;
import com.lcb.soa.misc.common.util.GeneralHelper;
import com.lcb.soa.misc.common.util.Pair;
import com.lcb.soa.misc.common.util.SystemUtil;
import com.lcb.soa.misc.common.util.Utilities;
import com.lcb.soa.misc.resp.NormalReturn;
import com.lcb.soa.misc.server.MyApplicationContext;

import static com.lcb.soa.misc.common.exception.ServiceException.*;
import static com.lcb.soa.misc.server.ServerHelper.*;
import static com.lcb.soa.misc.server.WebServerHelper.*;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanMap;

@Slf4j
@Order(-1)
@RestControllerAdvice
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object>
{
	private static final String MONITOR_LOG_TYPE = "HAPI-REQUEST";
	private static final Logger monitorLog = LoggerFactory.getLogger(MONITOR_LOGGER_NAME);
	private static final MdcTracingThreadPoolExecutor LOG_EXECUTOR = ControllerRequestAdvice.LOG_EXECUTOR;
		
	@Value("${web.config.advice.controller.retainResultCode:true}")
	private boolean retainResultCode;
	
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
			
			if(retainResultCode)
			{
				if(GeneralHelper.isStrEmpty(respBody.getResultCode()))
					respBody.setResultCode(respBody.getStatusCode());
			}
			else
			{
				respBody.setResultCode(null);
			}
			
			respBody.calcCostTime(RequestContext.getTimestamp());
			
			checkToken(respBody, req, resp);
			
			req.setAttribute(REQUEST_ATTRIBUTE_RESP_BODY, respBody);
		}
		
		logResponse(body, returnType, req);
		
		RequestContext.removeRequestAttribute();

		return body;
	}

	@SuppressWarnings("unchecked")
	private void checkToken(Response<?> respBody, HttpServletRequest request, HttpServletResponse response)
	{
		Pair<Integer, String> tokenCookieAttr = null;
		
		String statusCode = respBody.getStatusCode();
		
		if(ServiceException.API_OK.equals(statusCode))
		{
			Integer rt = respBody.getReturnType();
			
			if(rt != null)
			{
				if(Response.RT_LOGIN.equals(rt))
				{
					Object result = respBody.getResult();
					
					if(result != null)
					{
						String token = null;
						
						if(result instanceof Map<?, ?>)
							token = ((Map<String, String>)result).get(LCB_TOKEN);
						else
						{
							BeanMap map = BeanMap.create(result);
							token = (String)map.get(LCB_TOKEN);
						}
						
						if(GeneralHelper.isStrNotEmpty(token))
							tokenCookieAttr = new Pair<Integer, String>(COOKIE_MAX_AGE, token);
					}
				}
				else if(NormalReturn.RT_LOGOUT.equals(rt))
					tokenCookieAttr = new Pair<Integer, String>(0, "");						
			}
		}
		else if(ServiceException.LOGIN_INVALID.equals(statusCode))
			tokenCookieAttr = new Pair<Integer, String>(0, "");
		
		if(tokenCookieAttr != null)
		{
			Cookie cookie = createCookie(request, LCB_TOKEN, tokenCookieAttr.getSecond(), tokenCookieAttr.getFirst());
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
			LOG_EXECUTOR.execute(new Runnable()
			{
				@Override
				public void run()
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
		final String ua		 = getUserAgent(req);
		final String appId	 = MDC.get(MDC_APP_ID_KEY);
		final String appName = MDC.get(MDC_APP_NAME_KEY);
		
		Runnable task = new Runnable()
		{	
			@Override
			public void run()
			{
				JSONObject json		= (JSONObject)JSONObject.toJSON(requestAttribute.getBody());
				JSONObject jsonLog	= new JSONObject();
				
				if(json == null)
					json = new JSONObject();

				jsonLog.put(MDC_APP_CODE_KEY, requestAttribute.getAppCode());
				jsonLog.put(MDC_APP_ID_KEY, appId);
				jsonLog.put(MDC_APP_NAME_KEY, appName);
				jsonLog.put(MDC_SERVICE_ID_KEY, MyApplicationContext.currentApplicationId());
				jsonLog.put(MDC_SERVICE_NAME_KEY, MyApplicationContext.currentApplicationName());
				jsonLog.put(MDC_SERVICE_ADDR_KEY, SystemUtil.getAddress());
				jsonLog.put(MDC_ORG_NAME_KEY, MyApplicationContext.currentApplicationOrganization());
				jsonLog.put(MDC_OWNER_NAME_KEY, MyApplicationContext.currentApplicationOwner());
				jsonLog.put(MDC_CLIENT_ID_KEY, requestAttribute.getClientId());
				jsonLog.put(MDC_REQUEST_ID_KEY, requestAttribute.getRequestId());
				jsonLog.put(MDC_SESSION_ID_KEY, requestAttribute.getSessionId());
				jsonLog.put(MDC_TOKEN_KEY, requestAttribute.getToken());
				jsonLog.put(MDC_TOKEN_ALIAS_KEY, requestAttribute.getTokenAlias());
				jsonLog.put("logType", MONITOR_LOG_TYPE);
				jsonLog.put("clientAppId", requestAttribute.getAppId());
				jsonLog.put("url", requestAttribute.getRequestUri());
				jsonLog.put("apiName", rt.getMethod().getName());
				jsonLog.put("ip", requestAttribute.getRemoteIp());
				jsonLog.put("userId", requestAttribute.getUserId());
				jsonLog.put("mobile", json.getString("mobile"));
				jsonLog.put("request", json.toString());
				jsonLog.put("result", JSONObject.toJSONString(body, JSON_SERIAL_FEATURES_DEFAULT));
				jsonLog.put("currentTime", requestAttribute.getTimestamp());
				
				if(body instanceof Response<?>)
				{
					Response<?> respBody = (Response<?>)body;
					
					jsonLog.put("resultCode", respBody.getResultCode());
					jsonLog.put("statusCode", respBody.getStatusCode());
					jsonLog.put("costTime", respBody.getCostTime());
					jsonLog.put("msg", VALIDATE_ERROR.equals(respBody.getStatusCode()) ? respBody.getMsg() + ": " + JSONObject.toJSONString(respBody.getValidationErrors()) : respBody.getMsg());
				}

				try
				{
					if(StringUtils.isNotBlank(ua))
					{
						jsonLog.put("ua", ua);
						jsonLog.put("nativeVersion", Utilities.getNativeAppVersion(ua));
						jsonLog.put("application", StringUtils.substringAfterLast(ua, " "));

						UserAgent userAgent = new UserAgent(ua);
						
						if(userAgent != null)
						{
							jsonLog.put("browser", userAgent.getBrowser().getName());
							jsonLog.put("browserVersion", userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion().toString() : "");
							
							String os = userAgent.getOperatingSystem().getGroup().getName();
							jsonLog.put("os", os);
							
							if("Android".equals(os))
								jsonLog.put("osVersion", Utilities.getAndroidVersion(ua));
							else if("iOS".equals(os))
								jsonLog.put("osVersion", Utilities.getIOSVersion(ua));

							jsonLog.put("device", userAgent.getOperatingSystem().getDeviceType().getName());
							jsonLog.put("manufacturer", userAgent.getOperatingSystem().getManufacturer().name());
						}
					}
				}
				catch(Exception e)
				{
				}

				String msg = jsonLog.toJSONString();
				
				if(body instanceof Response<?>)
				{
					Response<?> respBody = (Response<?>)body;
					String statusCode	 = respBody.getStatusCode();
					
					if(API_OK.equals(statusCode))
						monitorLog.info(msg);
					else if(GENERAL_ERROR.equals(statusCode))
						monitorLog.error(msg);
					else
						monitorLog.warn(msg);
				}
				else
				{
					monitorLog.info(msg);
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
