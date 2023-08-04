package com.github.hpsocket.soa.framework.web.support;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.core.util.CryptHelper;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.core.util.Result;

import com.alibaba.fastjson2.JSONWriter;
import com.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import com.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import com.github.hpsocket.soa.framework.web.service.TracingContext;

import static com.github.hpsocket.soa.framework.web.holder.AppConfigHolder.*;

public class WebServerHelper
{
	public static final String REQUEST_ATTRIBUTE_JSON_BODY	= "__request.attribute.json.body__";
	public static final String REQUEST_ATTRIBUTE_RESP_BODY	= "__request.attribute.resp.body__";
	public static final String REQUEST_ATTRIBUTE_CONTEXT	= "__request.attribute.context__";
	public static final String REQUEST_ATTRIBUTE_INFO		= "__request.attribute.info__";
	
	public static final String HEADER_REQUEST_INFO			= "X-Request-Info";
	public static final String HEADER_REQUEST_ID			= "requestId";
	public static final String HEADER_CLIENT_ID				= "clientId";
	public static final String HEADER_SESSION_ID			= "sessionId";
	public static final String HEADER_TOKEN					= "token";
	public static final String HEADER_APP_CODE				= "appCode";
	public static final String HEADER_SRC_APP_CODE			= "srcAppCode";
	public static final String HEADER_GROUP_ID				= "groupId";
	public static final String HEADER_VERSION				= "version";
	public static final String HEADER_EXTRA					= "extra";

	public static final int DEFAULT_COOKIE_MAX_AGE	= 10 * 365 * 24 * 60 * 60;
	public static final String DEFAULT_CHARSET		= GeneralHelper.DEFAULT_ENCODING;
	
	public static final boolean HTTP_ONLY_COOKIE	= false;
	
	public static final String GET		= "GET";
	public static final String PUT		= "PUT";
	public static final String POST		= "POST";
	public static final String DELETE	= "DELETE";
	public static final String HEAD		= "HEAD";
	public static final String PATCH	= "PATCH";
	public static final String OPTIONS	= "OPTIONS";
	public static final String TRACE	= "TRACE";
	public static final String CONNECT	= "CONNECT";

	public static final String REQUEST_FORMAT_JSON		= "json";
	public static final String REQUEST_FORMAT_FORM		= "form";
	public static final String DEFAULT_REQUEST_CHARSET	= DEFAULT_CHARSET;
	public static final String DEFAULT_REQUEST_FORMAT	= REQUEST_FORMAT_JSON;
	public static final String RESPONSE_CONTENT_TYPE	= "application/json;charset=" + DEFAULT_REQUEST_CHARSET.toLowerCase();
	public static final Pattern DOMAIN_PATTERN	= Pattern.compile("[0-9a-zA-Z]+((\\.com\\.cn)|(\\.com)|(\\.cn)|(\\.net)|(\\.org)|(\\.edu))$");
	public static final Pattern SPIDER_PATTERN	= Pattern.compile(".*(Googlebot|Baiduspider|Yahoo\\!\\ Slurp|iaskspider|YodaoBot|msnbot|\\ spider)([\\/\\+\\ \\;]).*", Pattern.CASE_INSENSITIVE);
	
	public static final String MONITOR_LOGGER_NAME	= "SOA-MONITOR";
	public static final String MONITOR_INGRESS		= "MONITOR-INGRESS";
	public static final String MONITOR_EGRESS		= "MONITOR-EGRESS";

	public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_DEFAULT		= {JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.WriteNonStringKeyAsString};
	public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_NO_NULL_VAL	= {JSONWriter.Feature.WriteNonStringKeyAsString};
	
	public static final ThreadPoolExecutor ASYNC_LOG_EXECUTOR = new ThreadPoolExecutor(	4,
																						16,
																						60,
																						TimeUnit.SECONDS,
																						new LinkedBlockingDeque<>(6000),
																						new ThreadPoolExecutor.CallerRunsPolicy());																		 

	public static final boolean checkUserAgent(String ua)
	{
		return (GeneralHelper.isStrNotEmpty(ua) && !SPIDER_PATTERN.matcher(ua).matches());
	}
		
	public static final boolean isHasBodyRequest(String method)
	{
		return	GeneralHelper.isStrNotEmpty(method)
				&& (POST.equalsIgnoreCase(method) || PUT.equalsIgnoreCase(method) || PATCH.equals(method));	
	}

	public static final boolean isJsonContentType(String ct)
	{
		if(GeneralHelper.isStrNotEmpty(ct))
			return false;
		
		int index = ct.indexOf('/');
		
		if(index < 0)
			return false;
		
		return ct.regionMatches(true, 0, REQUEST_FORMAT_JSON, index + 1, REQUEST_FORMAT_JSON.length());
	}

	public static final String retriveHostDomain(String host)
	{
		int index = host.indexOf(':');

		if(index != -1)
			host = host.substring(0, index);

		if(!GeneralHelper.isStrIPAddress(host))
		{
			Matcher m = DOMAIN_PATTERN.matcher(host);

			if(m.find())
				host = m.group();
		}
		
		return host;
	}
	
	public static final Result<Boolean, Cookie> checkClientCookie(HttpServletRequest req)
	{
		boolean exists		= false;
		Cookie clientCookie	= getCookie(req, HEADER_CLIENT_ID);
		
		if(clientCookie != null)
			exists = true;

		if(!exists)
			clientCookie = createCookie(req, HEADER_CLIENT_ID, UUID.randomUUID().toString(), getCookieMaxAge());

		return new Result<>(exists, clientCookie);
	}
	
	public static final Cookie createCookie(HttpServletRequest req, String name, String value, int maxAge)
	{
		Cookie cookie = new Cookie(name, value);

		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(HTTP_ONLY_COOKIE);

		String host = req.getHeader("Host");

		if(GeneralHelper.isStrNotEmpty(host))
		{		
			host = retriveHostDomain(host);
			cookie.setDomain(host);
		}
		
		return cookie;
	}

	public static final Cookie getCookie(HttpServletRequest req, String name)
	{
		return getCookie(req, name, false);
	}

	public static final Cookie getCookie(HttpServletRequest req, String name, boolean includeEmptyValue)
	{
		Cookie cookie	 = null;
		Cookie[] cookies = req.getCookies();

		if(cookies != null && cookies.length > 0)
		{
			for(Cookie c : cookies)
			{
				if(name.equals(c.getName()) && (includeEmptyValue || GeneralHelper.isStrNotEmpty(c.getValue())))
				{
					cookie = c;
					break;
				}
			}
		}

		return cookie;
	}
	
	public static final boolean isJsonRequest(HttpServletRequest req)
	{
		return isHasBodyRequest(req.getMethod()) && isJsonContentType(req.getContentType());
	}
	
	public static final String getUserAgent(HttpServletRequest req)
	{
		return req.getHeader("User-Agent");
	}
	
	public static final String getIpAddr(HttpServletRequest request)
	{
		String ip = request.getHeader("X-Real-IP");
		
		if(GeneralHelper.isStrEmpty(ip))
		{
			String forwards = request.getHeader("X-Forwarded-For");
			
			if(GeneralHelper.isStrNotEmpty(forwards))
			{
				int i = forwards.indexOf(',');
				ip = GeneralHelper.safeTrimString(i >= 0 ? forwards.substring(0, i) : forwards);
			}
			
			if(GeneralHelper.isStrEmpty(ip))
			{
				ip = request.getRemoteAddr();
			}
		}
		
		return ip;
	}
	
	public static final String getRequestUri(HttpServletRequest request)
	{
		String requestUri = request.getRequestURI();
		
		if(GeneralHelper.isStrEmpty(requestUri))
		{
			requestUri = REQUEST_PATH_SEPARATOR;
		}
		
		return requestUri;
	}
	
	public static final String getRequestPath(HttpServletRequest request)
	{
		String requestUri	= getRequestUri(request);
		String requestPath	= requestUri.substring(getServletUriPrefix().length());
		
		return requestPath;
	}
	
	public static final String getRequestMethod(HttpServletRequest request)
	{
		return request.getMethod();
	}
	
	@SuppressWarnings("unchecked")
	public static final Map<String, String> parseRequestInfo(HttpServletRequest request)
	{
		Map<String, String> infos = (Map<String, String>)request.getAttribute(REQUEST_ATTRIBUTE_INFO);
		
		if(infos != null)
			return infos;
		
		infos = new HashMap<>();
		
		String encoding = request.getCharacterEncoding();
		
		if(GeneralHelper.isStrEmpty(encoding))
			encoding = DEFAULT_REQUEST_CHARSET;
		
		String header = request.getHeader(HEADER_REQUEST_INFO);
		
		if(GeneralHelper.isStrNotEmpty(header))
		{
			StringTokenizer st = new StringTokenizer(header, ";");
			
			while(st.hasMoreTokens())
			{
				String field = st.nextToken();
				int i = field.indexOf('=');
				
				if(i > 0)
				{
					String key = field.substring(0, i).trim();
					
					if(GeneralHelper.isStrNotEmpty(key))
					{
						String value = field.substring(i + 1).trim();
						
						infos.put(key, CryptHelper.urlDecode(value));
					}
				}
			}
		}
		
		request.setAttribute(REQUEST_ATTRIBUTE_INFO, infos);
		
		return infos;
	}

	public static final MdcAttr createMdcAddr()
	{
		return createMdcAddr(false);
	}
	
	public static final MdcAttr createMdcAddr(boolean generateRequestId)
	{
		MdcAttr mdcAttr = new MdcAttr();
		
		mdcAttr.setAppId(AppConfigHolder.getAppId());
		mdcAttr.setAppName(AppConfigHolder.getAppName());
		mdcAttr.setServiceId(AppConfigHolder.getAppId());
		mdcAttr.setServiceName(AppConfigHolder.getAppName());
		mdcAttr.setServiceAddr(AppConfigHolder.getAppAddress());
		
		mdcAttr.setFromServiceId(AppConfigHolder.getAppId());
		mdcAttr.setFromServiceName(AppConfigHolder.getAppName());
		mdcAttr.setFromServiceAddr(AppConfigHolder.getAppAddress());
		
		if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOrganization()))
			mdcAttr.setOrganization(AppConfigHolder.getAppOrganization());
		if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOwner()))
			mdcAttr.setOwner(AppConfigHolder.getAppOwner());
		
		if(generateRequestId)
			mdcAttr.setRequestId(UUID.randomUUID().toString());
		
		TracingContext tracingContext = SpringContextHolder.getTracingContext();
		
		if(tracingContext != null)
		{
			String traceId = tracingContext.getTraceId();
			
			if(GeneralHelper.isStrNotEmpty(traceId))
				mdcAttr.setTraceId(tracingContext.getTraceId());
		}
		
		return mdcAttr;
	}

}
