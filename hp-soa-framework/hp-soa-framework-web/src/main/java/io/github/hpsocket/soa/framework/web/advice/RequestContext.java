package io.github.hpsocket.soa.framework.web.advice;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Result;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static io.github.hpsocket.soa.framework.web.support.WebServerHelper.*;
import static org.springframework.web.context.request.ServletRequestAttributes.*;

/** <b>HTTP 请求上下文</b> */
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
    
    static final <T> void setAttribute(String name, T value)
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

    public static final String getClientAddr()
    {
        return getRequestAttribute().getClientAddr();
    }

    public static final String getRequestUri()
    {
        return getRequestAttribute().getRequestUri();
    }
    
    public static final String getRequestPath()
    {
        return getRequestAttribute().getRequestPath();
    }
    
    public static final String getRequestMethod()
    {
        return getRequestAttribute().getRequestMethod();
    }
    
    public static final Long getGroupId()
    {
        return getRequestAttribute().getGroupId();
    }

    public static final Long getUserId()
    {
        return getRequestAttribute().getUserId();
    }
    
    public static final long getTimestamp()
    {
        return getRequestAttribute().getTimestamp();
    }
    
    public static final Object getBody()
    {
        return getRequestAttribute().getBody();
    }
    
    static final void setBody(Object body)
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
    
    public static final RequestAttribute parseRequestAttribute(HttpServletRequest request, HttpServletResponse response)
    {
        long timestamp               = System.currentTimeMillis();
        Map<String, String> reqInfos = parseRequestInfo(request);
        
        String appCode      = parseRequestField(request, reqInfos, HEADER_APP_CODE, false);
        String srcAppCode   = parseRequestField(request, reqInfos, HEADER_SRC_APP_CODE, false);
        String version      = parseRequestField(request, reqInfos, HEADER_VERSION, false);
        String extra        = parseRequestField(request, reqInfos, HEADER_EXTRA, false);
        String token        = parseRequestField(request, reqInfos, HEADER_TOKEN, true);
        String groupId      = parseRequestField(request, reqInfos, HEADER_GROUP_ID, false);
        String sessionId    = parseRequestField(request, reqInfos, HEADER_SESSION_ID, true);
        String requestId    = parseRequestField(request, reqInfos, HEADER_REQUEST_ID, false);
        String clientId     = parseRequestField(request, reqInfos, HEADER_CLIENT_ID, false);
        
        if(GeneralHelper.isStrEmpty(clientId))
        {
            if(checkUserAgent(getUserAgent(request)))
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
            requestId = WebServerHelper.randomUUID();

        String requestUri    = WebServerHelper.getRequestUri(request);
        String requestPath   = WebServerHelper.getRequestPath(request);
        String requestMethod = WebServerHelper.getRequestMethod(request);
        
        RequestAttribute reqAttr = new RequestAttribute(appCode, srcAppCode, token,
                                                        clientId, requestId, sessionId,
                                                        GeneralHelper.str2Long(groupId));
        reqAttr.setVersion(version);
        reqAttr.setExtra(extra);
        reqAttr.setClientAddr(WebServerHelper.getRequestAddr(request));
        reqAttr.setRequestUri(requestUri);
        reqAttr.setRequestPath(requestPath);
        reqAttr.setRequestMethod(requestMethod);
        reqAttr.setTimestamp(timestamp);
        
        setRequestAttribute(reqAttr);
        
        return reqAttr;
    }

}
