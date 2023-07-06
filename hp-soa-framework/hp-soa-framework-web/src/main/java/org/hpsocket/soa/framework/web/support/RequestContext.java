package org.hpsocket.soa.framework.web.support;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.core.util.Result;
import org.hpsocket.soa.framework.web.model.RequestAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hpsocket.soa.framework.web.support.WebServerHelper.*;
import static org.springframework.web.context.request.ServletRequestAttributes.*;

public class RequestContext
{
	public static final ServletRequestAttributes getServletRequestAttributes()
	{
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes());
	}
	
	public static final HttpServletRequest getServletRequest()
	{
		return getServletRequestAttributes().getRequest();
	}

	public static final HttpServletResponse getServletResponse()
	{
		return getServletRequestAttributes().getResponse();
	}

	public static final RequestAttribute getRequestAttribute()
	{
		return getAttribute(REQUEST_ATTRIBUTE_CONTEXT);
	}
	
	static final void setRequestAttribute(RequestAttribute attribute)
	{
		setAttribute(REQUEST_ATTRIBUTE_CONTEXT, attribute);
	}
	
	static final void removeRequestAttribute()
	{
		removeAttribute(REQUEST_ATTRIBUTE_CONTEXT);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T getAttribute(String name)
	{
		return (T)getServletRequestAttributes().getAttribute(name, SCOPE_REQUEST);
	}
	
	public static final <T> void setAttribute(String name, T value)
	{
		getServletRequestAttributes().setAttribute(name, value, SCOPE_REQUEST);
	}
	
	public static final void removeAttribute(String name)
	{
		getServletRequestAttributes().removeAttribute(name, SCOPE_REQUEST);
	}
	
	public static final String getAppCode()
	{
		return getRequestAttribute().getAppCode();
	}

	public static final String getSrcAppCode()
	{
		return getRequestAttribute().getSrcAppCode();
	}

	public static final String getToken()
	{
		return getRequestAttribute().getToken();
	}

	public static final String getTokenAlias()
	{
		return getRequestAttribute().getTokenAlias();
	}

	public static final String getVersion()
	{
		return getRequestAttribute().getVersion();
	}

	public static final String getExtra()
	{
		return getRequestAttribute().getExtra();
	}

	public static final String getRequestId()
	{
		return getRequestAttribute().getRequestId();
	}

	public static final String getSessionId()
	{
		return getRequestAttribute().getSessionId();
	}

	public static final String getClientId()
	{
		return getRequestAttribute().getClientId();
	}

	public static final String getRemoteIp()
	{
		return getRequestAttribute().getRemoteIp();
	}

	public static final String getRequestUri()
	{
		return getRequestAttribute().getRequestUri();
	}
	
	public static final String getRequestPath()
	{
		return getRequestAttribute().getRequestPath();
	}
	
	public static final Long getGroupId()
	{
		return getRequestAttribute().getGroupId();
	}

	public static final Long getUserId()
	{
		return getRequestAttribute().getUserId();
	}
	
	public static final Long getInternalUserId()
	{
		return getRequestAttribute().getInternalUserId();
	}
	
	public static final long getTimestamp()
	{
		return getRequestAttribute().getTimestamp();
	}
	
	public static final Object getBody()
	{
		return getRequestAttribute().getBody();
	}
	
	public static final void setBody(Object body)
	{
		getRequestAttribute().setBody(body);
	}
	
	public static final void setServletResponseStatus(int sc)
	{
		getServletResponse().setStatus(sc);
	}
	
	public static final void sendServletResponseError(int sc, String sm) throws IOException
	{
		getServletResponse().sendError(sc, sm);
	}
	
	public static final RequestAttribute parseRequestAttribute()
	{
		HttpServletRequest request	 = getServletRequest();
		HttpServletResponse response = getServletResponse();
		Map<String, String> reqInfos = parseRequestInfo(request);
		
		String appCode		= parseRequestField(request, reqInfos, HEADER_APP_CODE, false);
		String srcAppCode	= parseRequestField(request, reqInfos, HEADER_SRC_APP_CODE, false);
		String version		= parseRequestField(request, reqInfos, HEADER_VERSION, false);
		String extra		= parseRequestField(request, reqInfos, HEADER_EXTRA, false);
		String token		= parseRequestField(request, reqInfos, HEADER_TOKEN, true);
		String tokenAlias	= parseRequestField(request, reqInfos, HEADER_TOKEN_ALIAS, true);
		String groupId		= parseRequestField(request, reqInfos, HEADER_GROUP_ID, false);
		String sessionId	= parseRequestField(request, reqInfos, HEADER_SESSION_ID, true);
		String requestId	= parseRequestField(request, reqInfos, HEADER_REQUEST_ID, false);
		String clientId 	= null;
		
		if(checkUserAgent(getUserAgent(request)))
		{
			clientId = reqInfos.get(HEADER_CLIENT_ID);
			
			if(GeneralHelper.isStrEmpty(clientId))
			{
				Result<Boolean, Cookie> clientCookieRS = checkClientCookie(request);
				Cookie cookie = clientCookieRS.getValue();
				String cookieValue = cookie.getValue();
				
				clientId = cookieValue;
				
				if(!clientCookieRS.getFlag())
					response.addCookie(cookie);
			}
		}		
		
		if(GeneralHelper.isStrEmpty(requestId))
			requestId = UUID.randomUUID().toString();

		String requestPath	= WebServerHelper.getRequestPath(request);
		String requestUri	= WebServerHelper.getRequestUri(request);
		
		RequestAttribute reqAttr = new RequestAttribute(appCode, srcAppCode, token,
														clientId, requestId, sessionId,
														GeneralHelper.str2Long(groupId));
		reqAttr.setTokenAlias(tokenAlias);
		reqAttr.setVersion(version);
		reqAttr.setExtra(extra);
		reqAttr.setRemoteIp(WebServerHelper.getIpAddr(request));
		reqAttr.setRequestPath(requestPath);
		reqAttr.setRequestUri(requestUri);
		reqAttr.setTimestamp(System.currentTimeMillis());
		
		setRequestAttribute(reqAttr);
		
		return reqAttr;
	}

	private static String parseRequestField(HttpServletRequest request, Map<String, String> attrs, String name, boolean checkCookie)
	{
		String value = attrs.get(name);
		
		if(GeneralHelper.isStrEmpty(value))
		{
			value = request.getHeader(name);
			
			if(GeneralHelper.isStrEmpty(value) && checkCookie)
			{
				Cookie cookie = getCookie(request, name);
				
				if(cookie != null)
					value = cookie.getValue();
			}
		}

		return value;
	}
}
