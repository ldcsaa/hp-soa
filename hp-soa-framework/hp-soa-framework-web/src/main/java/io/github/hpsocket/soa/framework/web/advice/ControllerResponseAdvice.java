package io.github.hpsocket.soa.framework.web.advice;

import java.io.IOException;
import java.util.Map;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.mdc.MdcRunnable;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.model.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson2.JSONObject;
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;
import static io.github.hpsocket.soa.framework.web.support.WebServerHelper.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>HTTP 响应拦截器</b> */
@Slf4j
@RestControllerAdvice
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object>, Ordered
{
    private static final Logger MONITOR_LOGGER = LoggerFactory.getLogger(MONITOR_LOGGER_NAME);
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
    public int getOrder()
    {
        return -10;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        RequestAttribute attr = RequestContext.getRequestAttribute();
        
        if(attr == null)
            return false;
        
        String requestUri = attr.getRequestUri();
        
        for(String ignoreLogPath : AppConfigHolder.getExcludedLogPaths())
        {
            if(requestUri.startsWith(ignoreLogPath))
                return false;
        }
        
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response)
    {
        HttpServletRequest req     = ((ServletServerHttpRequest)request).getServletRequest();
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
                            tokenCookieAttr = new Pair<Integer, String>(AppConfigHolder.getCookieMaxAge(), token);
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

    private void logResponse(Object body, MethodParameter rt, HttpServletRequest req)
    {
        final RequestAttribute requestAttribute = RequestContext.getRequestAttribute();
        
        try
        {
            ASYNC_LOG_EXECUTOR.execute(new MdcRunnable()
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
                JSONObject json        = JSONObject.from(requestAttribute.getBody());
                JSONObject jsonLog    = new JSONObject();
                
                if(json == null)
                    json = new JSONObject();
            
                jsonLog.put("monitor_type", MONITOR_INGRESS);

                @SuppressWarnings("unchecked")
                final Map<String, ?> reqAttr = BeanMap.create(requestAttribute);;

                reqAttr.forEach((k, v) -> {
                    if(v != null && !k.equals("body"))
                        jsonLog.put(k, v.toString());
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
                    JSONObject jsonUa = new JSONObject();
                    final Capabilities caps = uaParser.parse(ua);
                    
                    jsonUa.put("name", ua);
                    jsonUa.put("browser", caps.getBrowser().concat(GeneralHelper.isStrNotEmpty(caps.getBrowserMajorVersion()) ? " " + caps.getBrowserMajorVersion() : ""));
                    jsonUa.put("browserType", caps.getBrowserType());
                    jsonUa.put("deviceType", caps.getDeviceType());
                    jsonUa.put("platform", caps.getPlatform().concat(GeneralHelper.isStrNotEmpty(caps.getPlatformVersion()) ? " " + caps.getPlatformVersion() : ""));
                    
                    jsonLog.put("ua", jsonUa);
                }

                String msg = jsonLog.toJSONString();
                
                if(body instanceof Response<?>)
                {
                    Response<?> respBody = (Response<?>)body;
                    Integer statusCode     = respBody.getStatusCode();
                    
                    if(GeneralHelper.equals(statusCode, OK))
                        MONITOR_LOGGER.info(msg);
                    else if(GeneralHelper.equals(statusCode, GENERAL_ERROR))
                        MONITOR_LOGGER.error(msg);
                    else
                        MONITOR_LOGGER.warn(msg);
                }
                else
                {
                    MONITOR_LOGGER.info(msg);
                }
            }
        };
                
        try
        {
            ASYNC_LOG_EXECUTOR.execute(task);
        }
        catch(Exception e)
        {
            log.error("async write {} log fail", MONITOR_INGRESS, e);
        }
    }
    
}
