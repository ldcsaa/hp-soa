package io.github.hpsocket.soa.starter.web.cloud.support;

import java.time.LocalDateTime;
import java.util.StringTokenizer;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.web.cloud.exception.CloudExceptionInfo;
import jakarta.servlet.http.HttpServletRequest;

import static io.github.hpsocket.soa.framework.core.mdc.MdcAttr.*;

/** <b>调用链跟踪辅助类</b> */
public class TracingHelper
{
    public static final String HEADER_TRACING_INFO  = "X-Tracing-Info";
    
    public static final String createTracingInfoHeader()
    {
        StringBuilder sb = new StringBuilder(500);
        
        for(String key : TRANSFER_MDC_KEYS)
        {
            String value = MDC.get(key);
            
            if(GeneralHelper.isStrNotEmpty(value))
                sb.append(key).append('=').append(CryptHelper.urlEncode(value)).append(';');
        }
        
        sb.append(MDC_FROM_SERVICE_ID_KEY).append('=').append(CryptHelper.urlEncode(AppConfigHolder.getAppId())).append(';');
        sb.append(MDC_FROM_SERVICE_NAME_KEY).append('=').append(CryptHelper.urlEncode(AppConfigHolder.getAppName())).append(';');
        sb.append(MDC_FROM_SERVICE_ADDR_KEY).append('=').append(CryptHelper.urlEncode(AppConfigHolder.getAppAddress()));
        
        return sb.toString();
    }
    
    public static final void fillMdcAttr(MdcAttr mdcAttr, String tracingInfo)
    {
        if(GeneralHelper.isStrEmpty(tracingInfo))
            return;

        StringTokenizer st = new StringTokenizer(tracingInfo, ";");
        
        while(st.hasMoreTokens())
        {
            String field = st.nextToken();
            int i = field.indexOf('=');
            
            if(i > 0)
            {
                String key = field.substring(0, i).trim();
                
                if(GeneralHelper.isStrNotEmpty(key))
                {
                    String value = CryptHelper.urlDecode(field.substring(i + 1).trim());                
                    mdcAttr.set(key, value);
                }
            }
        }
    }
    
    public static final void setRequestAttribute(MdcAttr mdcAttr, HttpServletRequest request)
    {
        String requestUri    = WebServerHelper.getRequestUri(request);
        String requestPath   = WebServerHelper.getRequestPath(request);
        String requestMethod = WebServerHelper.getRequestMethod(request);
        String clientAddr    = WebServerHelper.getRequestAddr(request);
        
        RequestAttribute reqAttr = new RequestAttribute(mdcAttr.getAppCode(), mdcAttr.getSrcAppCode(), mdcAttr.getToken(),
                                                        mdcAttr.getClientId(), mdcAttr.getRequestId(), mdcAttr.getSessionId(),
                                                        GeneralHelper.str2Long(mdcAttr.getGroupId()));
        
        reqAttr.setUserId(GeneralHelper.str2Long(mdcAttr.getUserId()));
        reqAttr.setVersion(mdcAttr.getVersion());
        reqAttr.setExtra(mdcAttr.getExtra());
        reqAttr.setClientAddr(clientAddr);
        reqAttr.setRequestUri(requestUri);
        reqAttr.setRequestPath(requestPath);
        reqAttr.setRequestMethod(requestMethod);
        
        RequestContext.setRequestAttribute(reqAttr);
    }
    
    public static final Response<CloudExceptionInfo> createExceptionResponse(Exception e)
    {
        return createExceptionResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    public static final Response<CloudExceptionInfo> createExceptionResponse(Exception e, HttpStatus httpStatus)
    {
        return createExceptionResponse(e, httpStatus.value());
    }
    
    public static final Response<CloudExceptionInfo> createExceptionResponse(Exception e, Integer status)
    {
        CloudExceptionInfo info = new CloudExceptionInfo();
        
        info.setStatus(status);
        info.setMessage(e.getMessage());
        info.setException(e.getClass().getName());
        info.setTimestamp(LocalDateTime.now());
        
        if(e instanceof ServiceException se)
        {
            info.setStatusCode(se.getStatusCode());
            info.setResultCode(se.getResultCode());
        }
        
        Response<CloudExceptionInfo> resp = new Response<>(ServiceException.INNER_API_CALL_EXCEPTION);
        resp.setResult(info);
        
        return resp;
    }
    
}
