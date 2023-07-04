package org.hpsocket.soa.framework.web.server;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.hpsocket.soa.framework.core.util.CryptHelper;
import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.core.util.Result;

import static org.hpsocket.soa.framework.web.server.ServerHelper.*;

public class WebServerHelper
{
	public static final String REQUEST_ATTRIBUTE_JSON_BODY	= "__request.attribute.json.body__";
	public static final String REQUEST_ATTRIBUTE_RESP_BODY	= "__request.attribute.resp.body__";
	public static final String REQUEST_ATTRIBUTE_CONTEXT	= "__request.attribute.context__";
	public static final String REQUEST_ATTRIBUTE_INFO		= "__request.attribute.info__";

	public static final Result<Boolean, Cookie> checkClientCookie(HttpServletRequest req)
	{
		boolean exists		= false;
		Cookie clientCookie	= getCookie(req, CLIENT_ID);
		
		if(clientCookie != null)
			exists = true;

		if(!exists)
			clientCookie = createCookie(req, CLIENT_ID, UUID.randomUUID().toString(), COOKIE_MAX_AGE);

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
			encoding = CryptHelper.DEFAULT_ENCODING;
		
		String header = request.getHeader(REQUEST_INFO_HEADER);
		
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
