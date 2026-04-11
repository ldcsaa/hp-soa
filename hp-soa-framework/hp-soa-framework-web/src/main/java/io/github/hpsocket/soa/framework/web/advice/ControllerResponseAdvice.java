package io.github.hpsocket.soa.framework.web.advice;

import java.util.Map;

import cn.hutool.http.useragent.Platform;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.mdc.MdcRunnable;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Pair;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentParser;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;
import static io.github.hpsocket.soa.framework.web.support.WebServerHelper.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>HTTP 响应拦截器</b> */
@Slf4j
@RestControllerAdvice
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object>, Ordered
{
    private static final Logger MONITOR_LOGGER = LoggerFactory.getLogger(MONITOR_LOGGER_NAME);
    
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
        return !AppConfigHolder.excludedPath(requestUri);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response)
    {
        HttpServletRequest req   = ((ServletServerHttpRequest)request).getServletRequest();
        HttpServletResponse resp = ((ServletServerHttpResponse)response).getServletResponse();
        
        if(body instanceof Response<?> respBody)
        {            
            if(respBody.getResultCode() == null)
                respBody.setResultCode(respBody.getStatusCode());
            if(AppConfigHolder.isReturnRequestId())
                respBody.setRequestId(RequestContext.getRequestAttribute().getRequestId());
            
            checkToken(respBody, req, resp);

            respBody.setCostTime(WebServerHelper.calcTimestamp());
        }
        
        if(WebServerHelper.isEntryOrNull())
            logResponse(body, returnType, req);
        
        return body;
    }

    private void checkToken(Response<?> respBody, HttpServletRequest request, HttpServletResponse response)
    {
        Pair<Integer, String> tokenCookieAttr = null;
        Integer statusCode = respBody.getStatusCode();
        
        if(GeneralHelper.equals(statusCode, ServiceException.OK))
        {
            Integer rt = respBody.getRespType();
            
            if(rt == null)
                return;
            
            if(rt == Response.RT_LOGIN)
            {
                Object data = respBody.getData();
                
                if(data != null)
                {
                    String token = null;
                    
                    if(data instanceof Map<?, ?> map)
                        token = (String)map.get(RESPONSE_TOKEN);
                    else
                    {
                        BeanMap map = BeanMap.create(data);
                        token = (String)map.get(RESPONSE_TOKEN);
                    }
                    
                    if(GeneralHelper.isStrNotEmpty(token))
                        tokenCookieAttr = new Pair<Integer, String>(AppConfigHolder.getCookieMaxAge(), token);
                }
                
                if(tokenCookieAttr == null)
                    log.warn("response type is 'Response.RT_LOGIN' but there is no token");
            }
            else if(rt == Response.RT_LOGOUT)
                tokenCookieAttr = new Pair<Integer, String>(0, "");
        }
        else if(GeneralHelper.equals(statusCode, ServiceException.LOGIN_INVALID))
            tokenCookieAttr = new Pair<Integer, String>(0, "");
        
        if(tokenCookieAttr != null)
        {
            ResponseCookie cookie = createCookie(request, HEADER_TOKEN, tokenCookieAttr.getSecond(), tokenCookieAttr.getFirst());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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
                    String strBody = GeneralHelper.truncateAndMore(JSONObject.toJSONString(body, JSON_SERIAL_FEATURES_NO_NULL_VAL), RESP_BODY_MAX_LOG_LENGTH);
                    log.info("[ RESPONSE ] -> {}", strBody);
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
                JSONObject jsonLog = new JSONObject();

                jsonLog.put("monitor_type", MONITOR_INGRESS);
                jsonLog.put("apiName", String.join("#", rt.getDeclaringClass().getName(), rt.getMethod().getName()));

                jsonLog.put("clientAddr", requestAttribute.getClientAddr());
                jsonLog.put("requestUri", requestAttribute.getRequestUri());
                jsonLog.put("requestPath", requestAttribute.getRequestPath());
                jsonLog.put("requestMethod", requestAttribute.getRequestMethod());
                if(GeneralHelper.isNotNullOrEmpty(requestAttribute.getRequestParams()))
                    jsonLog.put("requestParams", JSON.toJSONString(requestAttribute.getRequestParams(), JSON_SERIAL_FEATURES_NO_NULL_VAL));
                if(GeneralHelper.isNotNull(requestAttribute.getBody()))
                    jsonLog.put("request", GeneralHelper.truncateAndMore(JSON.toJSONString(requestAttribute.getBody(), JSON_SERIAL_FEATURES_NO_NULL_VAL), REQ_BODY_MAX_LOG_LENGTH));

                jsonLog.put("response", GeneralHelper.truncateAndMore(JSONObject.toJSONString(body, JSON_SERIAL_FEATURES_NO_NULL_VAL), RESP_BODY_MAX_LOG_LENGTH));

                if(body instanceof Response<?> respBody)
                {                    
                    jsonLog.put("resultCode", respBody.getResultCode());
                    jsonLog.put("statusCode", respBody.getStatusCode());
                    jsonLog.put("costTime", respBody.getCostTime());
                    jsonLog.put("msg", GeneralHelper.equals(respBody.getStatusCode(), PARAM_VERIFY_ERROR) ? respBody.getMessage() + ": " + JSONObject.toJSONString(respBody.getValidationErrors()) : respBody.getMessage());
                }
                
                if(GeneralHelper.isStrNotEmpty(ua))
                {
                    JSONObject jsonUa = new JSONObject();
                    UserAgent agent   = UserAgentParser.parse(ua);
                    Platform platform = agent.getPlatform();
                    
                    jsonUa.put("name", ua);
                    jsonUa.put("browser", agent.getBrowser().getName().concat(GeneralHelper.isStrNotEmpty(agent.getVersion()) ? " " + agent.getVersion() : ""));
                    jsonUa.put("engine", agent.getEngine().getName().concat(GeneralHelper.isStrNotEmpty(agent.getEngineVersion()) ? " " + agent.getEngineVersion() : ""));
                    jsonUa.put("os", agent.getOs().getName().concat(GeneralHelper.isStrNotEmpty(agent.getOsVersion()) ? " " + agent.getOsVersion() : ""));
                    jsonUa.put("platform", platform.getName());
                    jsonUa.put("isMobile", platform.isUnknown() ? null : agent.isMobile());
                                        
                    jsonLog.put("ua", jsonUa);
                }

                String msg = jsonLog.toJSONString();
                
                if(body instanceof Response<?> respBody)
                {
                    Integer statusCode = respBody.getStatusCode();
                    
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
