package org.hpsocket.soa.framework.core.mdc;

import java.util.Map;
import java.util.Set;

import org.slf4j.MDC;

public class MdcAttr
{
	public static final String MDC_REQUEST_ID_KEY	= "requestId";
	public static final String MDC_CLIENT_ID_KEY	= "clientId";
	public static final String MDC_SESSION_ID_KEY	= "sessionId";
	public static final String MDC_APP_CODE_KEY		= "appCode";
	public static final String MDC_TOKEN_KEY		= "token";
	public static final String MDC_TOKEN_ALIAS_KEY	= "tokenAlias";
	public static final String MDC_APP_ID_KEY		= "appId";
	public static final String MDC_APP_NAME_KEY		= "appName";
	public static final String MDC_SERVICE_ID_KEY	= "serviceId";
	public static final String MDC_SERVICE_NAME_KEY	= "serviceName";
	public static final String MDC_SERVICE_ADDR_KEY	= "serviceAddr";
	
	Map<String, String> mdcMap;

	/*
	public String clientId;
	public String requestId;
	public String sessionId;
	public String token;
	public String appCode;
	public String appName;
	public String appId;
	
	public String serviceId;
	public String serviceName;
	public String serviceAddr;
	*/
	
	public MdcAttr()
	{
		mdcMap = MDC.getCopyOfContextMap();
	}
	
	public MdcAttr(Map<String, String> mdcMap)
	{
		this.mdcMap = mdcMap;
	}
	
	public static MdcAttr from(Map<String, String> mdcMap)
	{
		return new MdcAttr(mdcMap);
	}
	
	public static MdcAttr fromMdc()
	{
		return new MdcAttr();
	}

	public void putMdc()
	{
		MDC.setContextMap(mdcMap);
	}
	
	public void removeMdc()
	{
		Set<String> keys = mdcMap.keySet();
		keys.stream().forEach(key -> MDC.remove(key));
		
		mdcMap.clear();
	}
	
	public void clear(boolean clearMdc)
	{
		mdcMap.clear();
		
		if(clearMdc)
			MDC.clear();
	}
	
	public void set(String key, String val)
	{
		mdcMap.put(key, val);
	}
	
	public String remove(String key)
	{
		return mdcMap.remove(key);
	}
	
	public String get(String key)
	{
		return mdcMap.get(key);
	}
	
	public Map<String, String> getCtxMap()
	{
		return mdcMap;
	}

	public void setCtxMap(Map<String, String> ctxMap)
	{
		this.mdcMap = ctxMap;
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

	public String getTokenAlias()
	{
		return get(MDC_TOKEN_ALIAS_KEY);
	}

	public void setTokenAlias(String tokenAlias)
	{
		set(MDC_TOKEN_ALIAS_KEY, tokenAlias);
	}

	public String getAppCode()
	{
		return get(MDC_APP_CODE_KEY);
	}

	public void setAppCode(String appCode)
	{
		set(MDC_APP_CODE_KEY, appCode);
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

	public String getOrgName()
	{
		return get(MDC_ORG_NAME_KEY);
	}

	public void setOrgName(String orgName)
	{
		set(MDC_ORG_NAME_KEY, orgName);
	}

	public String getOwnerName()
	{
		return get(MDC_OWNER_NAME_KEY);
	}

	public void setOwnerName(String ownerName)
	{
		set(MDC_OWNER_NAME_KEY, ownerName);
	}


}

