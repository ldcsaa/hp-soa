package org.hpsocket.soa.framework.web.support;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.hpsocket.soa.framework.core.util.CryptHelper;
import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.core.util.Result;

import com.alibaba.fastjson2.JSONWriter;
import com.lcb.soa.misc.server.SerializerFeature;

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
	public static final String HEADER_TOKEN_ALIAS			= "tokenAlias";
	public static final String HEADER_APP_CODE				= "appCode";
	public static final String HEADER_SRC_APP_CODE			= "srcAppCode";
	public static final String HEADER_GROUP_ID				= "groupId";
	public static final String HEADER_VERSION				= "version";
	public static final String HEADER_EXTRA					= "extra";

	public static final int COOKIE_MAX_AGE			= 10 * 365 * 24 * 60 * 60;
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
	
	public static final String MONITOR_LOGGER_NAME		= "SOA-MONITOR";
	public static final String BFF_MONITOR_LOG_TYPE		= "BFF-REQUEST";
	public static final String MONITOR_LOG_TYPE			= "HTTP-CLIENT";

	public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_DEFAULT		= {JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.WriteNonStringKeyAsString};
	public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_NO_NULL_VAL	= {JSONWriter.Feature.WriteNonStringKeyAsString};
	
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
			clientCookie = createCookie(req, HEADER_CLIENT_ID, UUID.randomUUID().toString(), COOKIE_MAX_AGE);

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
		String requestUri = request.getHeader("X-DOCUMENT-URI");
		
		if(GeneralHelper.isStrEmpty(requestUri))
		{
			requestUri = getRequestPath(request);
		}
		
		return requestUri;
	}
	
	public static final String getRequestPath(HttpServletRequest request)
	{
		String requestPath = request.getRequestURI();
			
		if(GeneralHelper.isStrEmpty(requestPath))
		{
			requestPath = request.getServletPath();
		}
		
		return requestPath;
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

}
