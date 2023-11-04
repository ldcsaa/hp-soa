package io.github.hpsocket.soa.framework.web.support;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.MDC;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.thread.SynchronousRejectedExecutionHandler;
import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Result;

import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.service.TracingContext;

import static com.alibaba.fastjson2.JSONWriter.Feature.*;

import static io.github.hpsocket.soa.framework.web.holder.AppConfigHolder.*;

/** <b>通用 Web 功能辅助类</b> */
public class WebServerHelper
{
    public static final String REQUEST_ATTRIBUTE_JSON_BODY  = "__request.attribute.json.body__";
    public static final String REQUEST_ATTRIBUTE_RESP_BODY  = "__request.attribute.resp.body__";
    public static final String REQUEST_ATTRIBUTE_CONTEXT    = "__request.attribute.context__";
    public static final String REQUEST_ATTRIBUTE_INFO       = "__request.attribute.info__";
    
    public static final String HEADER_REQUEST_INFO          = "X-Request-Info";
    public static final String HEADER_REQUEST_ID            = "X-Request-Id";
    public static final String HEADER_CLIENT_ID             = "X-Client-Id";
    public static final String HEADER_SESSION_ID            = "X-Session-Id";
    public static final String HEADER_TOKEN                 = "X-Token";
    public static final String HEADER_APP_CODE              = "X-App-Code";
    public static final String HEADER_SRC_APP_CODE          = "X-Src-App-Code";
    public static final String HEADER_GROUP_ID              = "X-Group-Id";
    public static final String HEADER_VERSION               = "X-Version";
    public static final String HEADER_EXTRA                 = "X-Extra";

    public static final int DEFAULT_COOKIE_MAX_AGE  = 10 * 365 * 24 * 60 * 60;
    public static final String DEFAULT_CHARSET      = GeneralHelper.DEFAULT_ENCODING;
    public static final Charset DEFAULT_CHARSET_OBJ = Charset.forName(DEFAULT_CHARSET);
    
    public static final boolean HTTP_ONLY_COOKIE    = false;
    
    public static final String GET      = "GET";
    public static final String PUT      = "PUT";
    public static final String POST     = "POST";
    public static final String DELETE   = "DELETE";
    public static final String HEAD     = "HEAD";
    public static final String PATCH    = "PATCH";
    public static final String OPTIONS  = "OPTIONS";
    public static final String TRACE    = "TRACE";
    public static final String CONNECT  = "CONNECT";

    public static final String REQUEST_FORMAT_JSON      = "json";
    public static final String REQUEST_FORMAT_FORM      = "form";
    public static final String DEFAULT_REQUEST_CHARSET  = DEFAULT_CHARSET;
    public static final String DEFAULT_REQUEST_FORMAT   = REQUEST_FORMAT_JSON;
    public static final String RESPONSE_CONTENT_TYPE    = "application/json;charset=" + DEFAULT_REQUEST_CHARSET.toLowerCase();
    public static final Pattern DOMAIN_PATTERN    = Pattern.compile("[0-9a-zA-Z]+((\\.com\\.cn)|(\\.com)|(\\.cn)|(\\.net)|(\\.org)|(\\.edu))$");
    public static final Pattern SPIDER_PATTERN    = Pattern.compile(".*(Googlebot|Baiduspider|iaskspider|YodaoBot|msnbot|\\ Crawler|\\ Slurp|\\ spider)([\\/\\+\\ \\;]).*", Pattern.CASE_INSENSITIVE);
    
    public static final String MONITOR_LOGGER_NAME  = "SOA-MONITOR";
    public static final String MONITOR_INGRESS      = "MONITOR-INGRESS";
    public static final String MONITOR_EGRESS       = "MONITOR-EGRESS";

    public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_DEFAULT        = {WriteByteArrayAsBase64, WriteNonStringKeyAsString, WriteMapNullValue};
    public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_NO_NULL_VAL    = {WriteByteArrayAsBase64, WriteNonStringKeyAsString};
    
    public static final ThreadPoolExecutor ASYNC_LOG_EXECUTOR = new ThreadPoolExecutor( 4,
                                                                                        16,
                                                                                        60,
                                                                                        TimeUnit.SECONDS,
                                                                                        new LinkedBlockingDeque<>(2000),
                                                                                        new ThreadPoolExecutor.CallerRunsPolicy());
    
    private static ObjectMapper jacksonObjectMapper;

    /** 检测 HTTP 请求的 User-Agent 是否合法 */
    public static final boolean checkUserAgent(String ua)
    {
        return (GeneralHelper.isStrNotEmpty(ua) && !SPIDER_PATTERN.matcher(ua).matches());
    }
    
    /** 检测 HTTP 请求是否包含 Body */
    public static final boolean isHasBodyRequest(String method)
    {
        return GeneralHelper.isStrNotEmpty(method) && 
               (POST.equalsIgnoreCase(method) || PUT.equalsIgnoreCase(method) || PATCH.equals(method));    
    }

    /** 检测 HTTP Content-Type 是否为 JSON 类型 */
    public static final boolean isJsonContentType(String ct)
    {
        if(GeneralHelper.isStrNotEmpty(ct))
            return false;
        
        int index = ct.indexOf('/');
        
        if(index < 0)
            return false;
        
        return ct.regionMatches(true, 0, REQUEST_FORMAT_JSON, index + 1, REQUEST_FORMAT_JSON.length());
    }

    /** 解析 HTTP 响应 Cookie 的 Domain */
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
    
    /** 检测 HTTP 请求 Cookie */
    public static final Result<Boolean, Cookie> checkClientCookie(HttpServletRequest req)
    {
        boolean exists        = false;
        Cookie clientCookie    = getCookie(req, HEADER_CLIENT_ID);
        
        if(clientCookie != null)
            exists = true;

        if(!exists)
            clientCookie = createCookie(req, HEADER_CLIENT_ID, randomUUID(), getCookieMaxAge());

        return new Result<>(exists, clientCookie);
    }
    
    /** 创建 HTTP 响应 Cookie */
    public static final Cookie createCookie(HttpServletRequest req, String name, String value, int maxAge)
    {
        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(HTTP_ONLY_COOKIE);

        String host = getHeader(req, "Host");

        if(GeneralHelper.isStrNotEmpty(host))
        {        
            host = retriveHostDomain(host);
            cookie.setDomain(host);
        }
        
        return cookie;
    }

    /** 获取 HTTP 请求 Cookie */
    public static final Cookie getCookie(HttpServletRequest req, String name)
    {
        return getCookie(req, name, false);
    }

    /** 获取 HTTP 请求 Cookie */
    public static final Cookie getCookie(HttpServletRequest req, String name, boolean includeEmptyValue)
    {
        Cookie cookie     = null;
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
    
    /** 检查是否为 JSON 类型 HTTP 请求 */
    public static final boolean isJsonRequest(HttpServletRequest req)
    {
        return isHasBodyRequest(req.getMethod()) && isJsonContentType(req.getContentType());
    }
    
    /** 获取 HTTP 请求 User-Agent */
    public static final String getUserAgent(HttpServletRequest req)
    {
        return getHeader(req, "User-Agent");
    }
    
    /** 获取 HTTP 请求客户端 IP 地址 */
    public static final String getRequestAddr(HttpServletRequest request)
    {
        String ip = getHeader(request, "X-Real-IP");
        
        if(GeneralHelper.isStrEmpty(ip))
        {
            String forwards = getHeader(request, "X-Forwarded-For");
            
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
    
    /** 获取 HTTP 请求 Url */
    public static final String getRequestUri(HttpServletRequest request)
    {
        String requestUri = request.getRequestURI();
        
        if(GeneralHelper.isStrEmpty(requestUri))
        {
            requestUri = REQUEST_PATH_SEPARATOR;
        }
        
        return requestUri;
    }
    
    /** 获取 HTTP 请求 Path */
    public static final String getRequestPath(HttpServletRequest request)
    {
        String requestUri    = getRequestUri(request);
        String requestPath    = requestUri.substring(getServletUriPrefix().length());
        
        return requestPath;
    }
    
    /** 获取 HTTP 请求 Method */
    public static final String getRequestMethod(HttpServletRequest request)
    {
        return request.getMethod();
    }
    
    /** 解析 HTTP 请求信息 */
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
        
        String header = getHeader(request, HEADER_REQUEST_INFO);
        
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
    
    /** 解析 HTTP 请求字段 */
    public static final String parseRequestField(HttpServletRequest request, Map<String, String> attrs, String name, boolean checkCookie)
    {
        String value = attrs.get(name);
        
        if(GeneralHelper.isStrEmpty(value))
        {
            value = getHeader(request, name);
            
            if(GeneralHelper.isStrEmpty(value) && checkCookie)
            {
                Cookie cookie = getCookie(request, name);
                
                if(cookie != null)
                    value = cookie.getValue();
            }
        }

        return value;
    }
    
    /** 获取 HTTP 请求头（兼容小写） */
    public static final String getHeader(HttpServletRequest request, String name)
    {
        return getHeader(request, name, true);
    }
    
    /** 获取 HTTP 请求头（可设置是否兼容小写） */
    public static final String getHeader(HttpServletRequest request, String name, boolean lcCompatible)
    {
        String value = request.getHeader(name);
        
        if(value == null && lcCompatible)
            value = request.getHeader(name.toLowerCase());
        
        return value;
    }

    /** 创建调用链 MDC 相关属性 */
    public static final MdcAttr createMdcAttr()
    {
        return createMdcAttr(true);
    }
    
    /** 创建调用链 MDC 相关属性 */
    public static final MdcAttr createMdcAttr(boolean generateRequestId)
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
            mdcAttr.setRequestId(randomUUID());
        
        String traceId = getTraceId();
            
        if(GeneralHelper.isStrNotEmpty(traceId))
            mdcAttr.setTraceId(traceId);
        
        return mdcAttr;
    }
    
    /** 调用链的 traceId 注入到 MDC */
    public static final void putMdcTraceId()
    {
        String traceId = getTraceId();
            
        if(GeneralHelper.isStrNotEmpty(traceId))
            MDC.put(MdcAttr.MDC_TRACE_ID_KEY, traceId);
    }

    /** 调用链的 traceId 从 MDC 中删除 */
    public static final void removeMdcTraceId()
    {
        MDC.remove(MdcAttr.MDC_TRACE_ID_KEY);    
    }

    /** 获取调用链的 traceId */
    public static final String getTraceId()
    {
        String traceId = null;
        TracingContext tracingContext = SpringContextHolder.getTracingContext();
        
        if(tracingContext != null)
            traceId = tracingContext.getTraceId();

        return traceId;
    }

    /** 获取调用链的当前 spanId */
    public static final String getSpanId()
    {
        String spanId = null;
        TracingContext tracingContext = SpringContextHolder.getTracingContext();
        
        if(tracingContext != null)
            spanId = tracingContext.getSpanId();

        return spanId;
    }

    /** 字符串转换为 {@linkplain RejectedExecutionHandler} 对象 */
    public static final RejectedExecutionHandler parseRejectedExecutionHandler(String rejectionPolicy, String defaultRejectionPolicy)
    {
        RejectedExecutionHandler rjh = null;
        
        if(GeneralHelper.isStrEmpty(rejectionPolicy))
            rejectionPolicy = defaultRejectionPolicy;
        
        if("CALLER_RUNS".equalsIgnoreCase(rejectionPolicy))
            rjh = new ThreadPoolExecutor.CallerRunsPolicy();
        else if("ABORT".equalsIgnoreCase(rejectionPolicy))
            rjh = new ThreadPoolExecutor.AbortPolicy();
        else if("DISCARD".equalsIgnoreCase(rejectionPolicy))
            rjh = new ThreadPoolExecutor.DiscardPolicy();
        else if("DISCARD_OLDEST".equalsIgnoreCase(rejectionPolicy))
            rjh = new ThreadPoolExecutor.DiscardOldestPolicy();
        else if("SYNC".equalsIgnoreCase(rejectionPolicy))
            rjh = new SynchronousRejectedExecutionHandler();
        else
            throw new RuntimeException(String.format("invalid rejection execution handler '%s'", rejectionPolicy));
        
        return rjh;
    }
    
    /** 检查应用程序是否只读 */
    public static final boolean isAppReadOnly()
    {
        return AppConfigHolder.isReadOnly();
    }
    
    /** 创建随机 UUID */
    public static final String randomUUID()
    {
        return IdGenerator.nextCompactUUID();
    }
    
    /** 获取默认 Jackson {@linkplain ObjectMapper} */
    public static final ObjectMapper getJacksonObjectMapper()
    {
        if(jacksonObjectMapper == null)
        {
            synchronized(WebServerHelper.class)
            {
                if(jacksonObjectMapper == null)
                {
                    jacksonObjectMapper = SpringContextHolder.getBean(ObjectMapper.class);
                }
            }
        }
        
        return jacksonObjectMapper;
    }
}
