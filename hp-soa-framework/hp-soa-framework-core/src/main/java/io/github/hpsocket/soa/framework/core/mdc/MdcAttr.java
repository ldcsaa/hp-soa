package io.github.hpsocket.soa.framework.core.mdc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;

/** <b>调用链跟踪 {@linkplain MDC} 属性</b> */
public class MdcAttr
{
    public static final String MDC_TRACE_ID_KEY    =     "__traceId";
    public static final String MDC_REQUEST_ID_KEY    = "__requestId";
    public static final String MDC_CLIENT_ID_KEY    = "__clientId";
    public static final String MDC_SESSION_ID_KEY    = "__sessionId";
    public static final String MDC_APP_CODE_KEY        = "__appCode";
    public static final String MDC_SRC_APP_CODE_KEY    = "__srcAppCode";
    public static final String MDC_TOKEN_KEY        = "__token";
    public static final String MDC_USER_ID_KEY        = "__userId";
    public static final String MDC_GROUP_ID_KEY        = "__groupId";
    public static final String MDC_EXTRA_KEY        = "__extra";
    public static final String MDC_APP_ID_KEY        = "__appId";
    public static final String MDC_APP_NAME_KEY        = "__appName";
    public static final String MDC_SERVICE_ID_KEY    = "__serviceId";
    public static final String MDC_SERVICE_NAME_KEY    = "__serviceName";
    public static final String MDC_SERVICE_ADDR_KEY    = "__serviceAddr";
    public static final String MDC_ORG_KEY            = "__organization";
    public static final String MDC_OWNER_KEY        = "__owner";

    public static final String MDC_MESSAGE_ID_KEY            = "__messageId";
    public static final String MDC_SOURCE_REQUEST_ID_KEY    = "__sourceRequestId";

    public static final String MDC_FROM_SERVICE_ID_KEY        = "__fromServiceId";
    public static final String MDC_FROM_SERVICE_NAME_KEY    = "__fromServiceName";
    public static final String MDC_FROM_SERVICE_ADDR_KEY    = "__fromServiceAddr";
    
    public static final String TRANSFER_MDC_KEYS[] = {
                    MDC_TRACE_ID_KEY,
                    MDC_REQUEST_ID_KEY,
                    MDC_CLIENT_ID_KEY,
                    MDC_SESSION_ID_KEY,
                    MDC_MESSAGE_ID_KEY,
                    MDC_SOURCE_REQUEST_ID_KEY,
                    MDC_APP_CODE_KEY,
                    MDC_SRC_APP_CODE_KEY,
                    MDC_TOKEN_KEY,
                    MDC_USER_ID_KEY,
                    MDC_GROUP_ID_KEY,
                    MDC_EXTRA_KEY,
                    MDC_APP_ID_KEY,
                    MDC_APP_NAME_KEY
    };
    
    public static final String TRANSFER_MDC_ALL_KEYS[] = {
                    MDC_TRACE_ID_KEY,
                    MDC_REQUEST_ID_KEY,
                    MDC_CLIENT_ID_KEY,
                    MDC_SESSION_ID_KEY,
                    MDC_MESSAGE_ID_KEY,
                    MDC_SOURCE_REQUEST_ID_KEY,
                    MDC_APP_CODE_KEY,
                    MDC_SRC_APP_CODE_KEY,
                    MDC_TOKEN_KEY,
                    MDC_USER_ID_KEY,
                    MDC_GROUP_ID_KEY,
                    MDC_EXTRA_KEY,
                    MDC_APP_ID_KEY,
                    MDC_APP_NAME_KEY,
                    MDC_FROM_SERVICE_ID_KEY,
                    MDC_FROM_SERVICE_NAME_KEY,
                    MDC_FROM_SERVICE_ADDR_KEY
    };
    
    Map<String, String> ctxMap;
    
    public MdcAttr()
    {
        ctxMap = new HashMap<>();
    }
    
    public MdcAttr(Map<String, String> mdcMap)
    {
        this.ctxMap = mdcMap;
    }
    
    public static MdcAttr from(Map<String, String> mdcMap)
    {
        return new MdcAttr(mdcMap);
    }
    
    public static MdcAttr fromMdc()
    {
        return new MdcAttr(MDC.getCopyOfContextMap());
    }

    public void putMdc()
    {
        ctxMap.forEach((k, v) -> MDC.put(k, v));
    }
    
    public void removeMdc()
    {
        removeMdc(true);
    }
    
    public void removeMdc(boolean clearMdcMap)
    {
        ctxMap.forEach((k, v) -> MDC.remove(k));    
        
        if(clearMdcMap)
            ctxMap.clear();
    }
    
    public void set(String key, String val)
    {
        ctxMap.put(key, val);
    }
    
    public String remove(String key)
    {
        return ctxMap.remove(key);
    }
    
    public String get(String key)
    {
        return ctxMap.get(key);
    }
    
    public Map<String, String> getCtxMap()
    {
        return ctxMap;
    }

    public void setCtxMap(Map<String, String> ctxMap)
    {
        this.ctxMap = ctxMap;
    }

    public String getTraceId()
    {
        return get(MDC_TRACE_ID_KEY);
    }

    public void setTraceId(String traceId)
    {
        set(MDC_TRACE_ID_KEY, traceId);
    }

    public String getClientId()
    {
        return get(MDC_CLIENT_ID_KEY);
    }

    public void setClientId(String clientId)
    {
        set(MDC_CLIENT_ID_KEY, clientId);
    }

    public String getRequestId()
    {
        return get(MDC_REQUEST_ID_KEY);
    }

    public void setRequestId(String requestId)
    {
        set(MDC_REQUEST_ID_KEY, requestId);
    }

    public String getSessionId()
    {
        return get(MDC_SESSION_ID_KEY);
    }

    public void setSessionId(String sessionId)
    {
        set(MDC_SESSION_ID_KEY, sessionId);
    }

    public String getMessageId()
    {
        return get(MDC_MESSAGE_ID_KEY);
    }

    public void setMessageId(String messageId)
    {
        set(MDC_MESSAGE_ID_KEY, messageId);
    }

    public String getSourceRequestId()
    {
        return get(MDC_SOURCE_REQUEST_ID_KEY);
    }

    public void setSourceRequestId(String sourceRequestId)
    {
        set(MDC_SOURCE_REQUEST_ID_KEY, sourceRequestId);
    }

    public String getToken()
    {
        return get(MDC_TOKEN_KEY);
    }

    public void setToken(String token)
    {
        set(MDC_TOKEN_KEY, token);
    }

    public String getUserId()
    {
        return get(MDC_USER_ID_KEY);
    }

    public void setUserId(String userId)
    {
        set(MDC_USER_ID_KEY, userId);
    }

    public String getGroupId()
    {
        return get(MDC_GROUP_ID_KEY);
    }

    public void setGroupId(String groupId)
    {
        set(MDC_GROUP_ID_KEY, groupId);
    }

    public String getExtra()
    {
        return get(MDC_EXTRA_KEY);
    }

    public void setExtra(String extra)
    {
        set(MDC_EXTRA_KEY, extra);
    }

    public String getAppCode()
    {
        return get(MDC_APP_CODE_KEY);
    }

    public void setAppCode(String appCode)
    {
        set(MDC_APP_CODE_KEY, appCode);
    }

    public void setSrcAppCode(String srcAppCode)
    {
        set(MDC_SRC_APP_CODE_KEY, srcAppCode);
    }

    public String getAppName()
    {
        return get(MDC_APP_NAME_KEY);
    }

    public void setAppName(String appName)
    {
        set(MDC_APP_NAME_KEY, appName);
    }

    public String getAppId()
    {
        return get(MDC_APP_ID_KEY);
    }

    public void setAppId(String appId)
    {
        set(MDC_APP_ID_KEY, appId);
    }

    public String getServiceId()
    {
        return get(MDC_SERVICE_ID_KEY);
    }

    public void setServiceId(String serviceId)
    {
        set(MDC_SERVICE_ID_KEY, serviceId);
    }

    public String getServiceName()
    {
        return get(MDC_SERVICE_NAME_KEY);
    }

    public void setServiceName(String serviceName)
    {
        set(MDC_SERVICE_NAME_KEY, serviceName);
    }

    public String getServiceAddr()
    {
        return get(MDC_SERVICE_ADDR_KEY);
    }

    public void setServiceAddr(String serviceAddr)
    {
        set(MDC_SERVICE_ADDR_KEY, serviceAddr);
    }

    public String getFromServiceId()
    {
        return get(MDC_FROM_SERVICE_ID_KEY);
    }

    public void setFromServiceId(String fromServiceId)
    {
        set(MDC_FROM_SERVICE_ID_KEY, fromServiceId);
    }

    public String getFromServiceName()
    {
        return get(MDC_FROM_SERVICE_NAME_KEY);
    }

    public void setFromServiceName(String fromServiceName)
    {
        set(MDC_FROM_SERVICE_NAME_KEY, fromServiceName);
    }

    public String getFromServiceAddr()
    {
        return get(MDC_FROM_SERVICE_ADDR_KEY);
    }

    public void setFromServiceAddr(String fromServiceAddr)
    {
        set(MDC_FROM_SERVICE_ADDR_KEY, fromServiceAddr);
    }

    public String getOrganization()
    {
        return get(MDC_ORG_KEY);
    }

    public void setOrganization(String orgName)
    {
        set(MDC_ORG_KEY, orgName);
    }

    public String getOwner()
    {
        return get(MDC_OWNER_KEY);
    }

    public void setOwner(String ownerName)
    {
        set(MDC_OWNER_KEY, ownerName);
    }

}

