package com.github.hpsocket.soa.framework.core.mdc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;

public class MdcAttr
{
	public static final String MDC_REQUEST_ID_KEY	= "__mdc_requestId";
	public static final String MDC_CLIENT_ID_KEY	= "__mdc_clientId";
	public static final String MDC_SESSION_ID_KEY	= "__mdc_sessionId";
	public static final String MDC_APP_CODE_KEY		= "__mdc_appCode";
	public static final String MDC_SRC_APP_CODE_KEY	= "__mdc_srcAppCode";
	public static final String MDC_TOKEN_KEY		= "__mdc_token";
	public static final String MDC_USER_ID_KEY		= "__mdc_userId";
	public static final String MDC_GROUP_ID_KEY		= "__mdc_groupId";
	public static final String MDC_EXTRA_KEY		= "__mdc_extra";
	public static final String MDC_APP_ID_KEY		= "__mdc_appId";
	public static final String MDC_APP_NAME_KEY		= "__mdc_appName";
	public static final String MDC_SERVICE_ID_KEY	= "__mdc_serviceId";
	public static final String MDC_SERVICE_NAME_KEY	= "__mdc_serviceName";
	public static final String MDC_SERVICE_ADDR_KEY	= "__mdc_serviceAddr";
	public static final String MDC_ORG_KEY			= "__mdc_organization";
	public static final String MDC_OWNER_KEY		= "__mdc_owner";
	
	public static final String TRANSFER_MDC_KEYS[] = {
					MDC_REQUEST_ID_KEY,
					MDC_CLIENT_ID_KEY,
					MDC_SESSION_ID_KEY,
					MDC_APP_CODE_KEY,
					MDC_SRC_APP_CODE_KEY,
					MDC_TOKEN_KEY,
					MDC_USER_ID_KEY,
					MDC_GROUP_ID_KEY,
					MDC_EXTRA_KEY,
					MDC_APP_ID_KEY,
					MDC_APP_NAME_KEY
	};

	
	Map<String, String> ctxMap;

	/*
	public String clientId;
	public String requestId;
	public String sessionId;
	public String token;
	public String appCode;
	public String srcAppCode;
	
	public String appId;
	public String appName;
	public String serviceId;
	public String serviceName;
	public String serviceAddr;
	public String organization;
	public String owner;
	*/
	
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

