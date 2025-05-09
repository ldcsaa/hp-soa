package io.github.hpsocket.soa.framework.web.support;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.exception.UnimportantException;
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
    public static final String HEADER_REGION                = "X-Region";
    public static final String HEADER_LANGUAGE              = "X-Language";
    public static final String HEADER_VERSION               = "X-Version";
    public static final String HEADER_EXTRA                 = "X-Extra";
    
    public static final String RESPONSE_TOKEN               = "token";

    public static final String DEFAULT_CHARSET              = GeneralHelper.DEFAULT_CHARSET;
    public static final Charset DEFAULT_CHARSET_OBJ         = GeneralHelper.DEFAULT_CHARSET_OBJ;
    
    public static final String COOKIE_SAME_SITE_STRICT      = "Strict";
    public static final String COOKIE_SAME_SITE_LAX         = "Lax";
    public static final String COOKIE_SAME_SITE_NONE        = "None";
    public static final int DEFAULT_COOKIE_MAX_AGE          = 10 * 365 * 24 * 60 * 60;
    public static final boolean DEFAULT_COOKIE_HTTP_ONLY    = false;
    public static final boolean DEFAULT_COOKIE_SECURE       = false;
    public static final String DEFAULT_COOKIE_SAME_SITE     = COOKIE_SAME_SITE_LAX;
    
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
    public static final Pattern DOMAIN_PATTERN          = Pattern.compile("[0-9a-zA-Z]+((\\.com(\\.[0-9a-zA-Z]+)?)|(\\.net(\\.[0-9a-zA-Z]+)?)|(\\.gov(\\.[0-9a-zA-Z]+)?)|(\\.org)|(\\.edu)|(\\.int)|(\\.biz)|(\\.info))$");
    public static final Pattern SPIDER_PATTERN          = Pattern.compile(".*(Googlebot|Baiduspider|iaskspider|YodaoBot|msnbot|\\ Crawler|\\ Slurp|\\ spider)([\\/\\+\\ \\;]).*", Pattern.CASE_INSENSITIVE);
    public static final Pattern FILTER_SKIP_PATTERN     = Pattern.compile("/api-docs.*|/swagger.*|.*\\.png|.*\\.css|.*\\.js|.*\\.html|/favicon.ico|/hystrix.stream");
    
    public static final String MONITOR_LOGGER_NAME      = "SOA-MONITOR";
    public static final String MONITOR_INGRESS          = "MONITOR-INGRESS";
    public static final String MONITOR_EGRESS           = "MONITOR-EGRESS";

    private static final ThreadLocal<Long> TIMESTAMP    = new ThreadLocal<>();
    
    public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_DEFAULT        = {WriteByteArrayAsBase64, WriteNonStringKeyAsString, WriteMapNullValue};
    public static final JSONWriter.Feature[] JSON_SERIAL_FEATURES_NO_NULL_VAL    = {WriteByteArrayAsBase64, WriteNonStringKeyAsString};
    
    private static final AtomicInteger THREAD_NUMBER          = new AtomicInteger(0);
    public static final ThreadPoolExecutor ASYNC_LOG_EXECUTOR = new ThreadPoolExecutor( 4,
                                                                                        16,
                                                                                        60,
                                                                                        TimeUnit.SECONDS,
                                                                                        new LinkedBlockingDeque<>(3000),
                                                                                        (r) -> {Thread t = new Thread(r);
                                                                                                t.setName("SOA-LOG-" + THREAD_NUMBER.incrementAndGet());
                                                                                                t.setPriority(Thread.NORM_PRIORITY);
                                                                                                t.setDaemon(true);
                                                                                                return t;},
                                                                                        (r, executor) -> System.err.println("fail to write log SOA log, rejected !"));
    
    private static ObjectMapper jacksonObjectMapper;
    private static LoggingSystem loggingSystem;
    
    public static final void StartTiming()
    {
        TIMESTAMP.set(System.currentTimeMillis());
    }
    
    public static final long calcTimestamp()
    {
        return System.currentTimeMillis() - TIMESTAMP.get();
    }

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
        int index  = host.lastIndexOf(':');

        if(index != -1)
        {
            int index2 = host.lastIndexOf(']');
            
            if(index2 == -1 || index2 < index)
                host = host.substring(0, index);
        }
        
        if(GeneralHelper.isStrIPv4Address(host))
            return host;
        else if(host.startsWith("[") && host.endsWith("]") && host.length() >= 4)
            return host.substring(1, host.length() - 1);
        else
        {
            Matcher m = DOMAIN_PATTERN.matcher(host);

            if(m.find())
                host = m.group();
        }
        
        return host;
    }
    
    /** 检测 HTTP 请求 Cookie */
    public static final Result<Boolean, ?> checkClientCookie(HttpServletRequest req)
    {
        boolean exists      = false;
        Object clientCookie = getCookie(req, HEADER_CLIENT_ID);
        
        if(clientCookie != null)
            exists = true;
        else
            clientCookie = createCookie(req, HEADER_CLIENT_ID, randomUUID(), getCookieMaxAge(), false, false, COOKIE_SAME_SITE_LAX);

        return new Result<>(exists, clientCookie);
    }
    
    /** 创建 HTTP 响应 Cookie */
    public static final ResponseCookie createCookie(HttpServletRequest req, String name, String value, int maxAge)
    {
        return createCookie(req, name, value, maxAge, isCookieSecure(), isCookieHttpOnly(), getCookieSameSite());
    }

    /** 创建 HTTP 响应 Cookie */
    public static final ResponseCookie createCookie(HttpServletRequest req, String name, String value, int maxAge, boolean secure, boolean httpOnly, String sameSite)
    {
        String domain = getHeader(req, "Host");

        if(GeneralHelper.isStrNotEmpty(domain))
            domain = retriveHostDomain(domain);

        return ResponseCookie.from(name, value)
                        .path("/")
                        .maxAge(maxAge)
                        .domain(domain)
                        .secure(secure)
                        .httpOnly(httpOnly)
                        .sameSite(sameSite)
                        .build();
    }

    /** 获取 HTTP 请求 Cookie */
    public static final Cookie getCookie(HttpServletRequest req, String name)
    {
        return getCookie(req, name, true);
    }

    /** 获取 HTTP 请求 Cookie */
    public static final Cookie getCookie(HttpServletRequest req, String name, boolean includeEmptyValue)
    {
        Cookie cookie    = null;
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
        
        if(value == null)
        {
            value = attrs.get(name.toLowerCase());
            
            if(value == null)
            {
                value = getHeader(request, name);
                
                if(value == null && checkCookie)
                {
                    Cookie cookie = getCookie(request, name);
                    
                    if(cookie != null)
                        value = cookie.getValue();
                }
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
        return createMdcAttr(true, true);
    }

    /** 创建调用链 MDC 相关属性 */
    public static final MdcAttr createMdcAttr(boolean generateRequestId)
    {
        return createMdcAttr(generateRequestId, true);
    }
    
    /** 创建调用链 MDC 相关属性 */
    public static final MdcAttr createMdcAttr(boolean generateRequestId, boolean isEntry)
    {
        MdcAttr mdcAttr = new MdcAttr();
        
        mdcAttr.setIsEntry(Boolean.toString(isEntry));
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
    
    public static final void assertAppIsNotReadOnly()
    {
        assertAppIsNotReadOnly(null);
    }
    
    public static final void assertAppIsNotReadOnly(String errorMsg)
    {
        if(!isReadOnly())
            return;
        
        String msg = GeneralHelper.isStrEmpty(errorMsg) ? ServiceException.READ_ONLY_EXCEPTION.getMessage() : errorMsg;
        throw new UnimportantException(msg, ServiceException.READ_ONLY_ERROR);
    }
    
    /** 检查应用程序是否只读 */
    public static final boolean isAppReadOnly()
    {
        return AppConfigHolder.isReadOnly();
    }
    
    /** 检查是否是调用链入口 */
    public static final boolean isEntry()
    {
        Boolean isEntry = isEntryOrNull();
        
        if(isEntry == null)
            throw new RuntimeException("unable to determine if current application is entry or not");
        
        return isEntry;
    }
    
    /** 检查是否是调用链入口 */
    public static final Boolean isEntryOrNull()
    {
        String isEntry = MDC.get(MdcAttr.MDC_IS_ENTRY_KEY);
        
        if(GeneralHelper.isStrEmpty(isEntry))
            return null;
        
        return Boolean.valueOf(isEntry);
    }
    
    /** 创建随机 UUID */
    public static final String randomUUID()
    {
        return IdGenerator.nextCompactUUID();
    }
    
    /** 获取内置 Jackson {@linkplain ObjectMapper} Bean */
    public static final ObjectMapper getJacksonObjectMapper()
    {
        if(jacksonObjectMapper == null)
        {
            synchronized(ObjectMapper.class)
            {
                if(jacksonObjectMapper == null)
                {
                    jacksonObjectMapper = SpringContextHolder.getBean(ObjectMapper.class);
                }
            }
        }
        
        return jacksonObjectMapper;
    }
    
    /** 获取内置 {@linkplain LoggingSystem} Bean */
    public static final LoggingSystem getLoggingSystem()
    {
        if(loggingSystem == null)
        {
            synchronized(LoggingSystem.class)
            {
                if(loggingSystem == null)
                {
                    loggingSystem = SpringContextHolder.getBean(LoggingSystem.class);
                }
            }
        }
        
        return loggingSystem;
    }

}
