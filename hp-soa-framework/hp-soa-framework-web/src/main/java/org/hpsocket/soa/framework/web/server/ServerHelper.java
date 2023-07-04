package org.hpsocket.soa.framework.web.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.mutable.MutableInt;
import org.hpsocket.soa.framework.core.mdc.MdcAttr;
import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.web.holder.AppConfigHolder;
import org.slf4j.MDC;


public class ServerHelper
{
	public static final int COOKIE_MAX_AGE			= 10 * 365 * 24 * 60 * 60;
	public static final String DEFAULT_CHARSET		= "UTF-8";
	public static final String COST_TIME			= "__costTime";
	public static final String STATUS_CODE			= "__statusCode";
	public static final String RESULT_CODE			= "__resultCode";
	public static final String REQUEST_ID			= "__requestId";
	public static final String CLIENT_ID			= "__clientId";
	public static final String SESSION_ID			= "__sessionId";
	public static final String TOKEN_NAME			= "__token";
	public static final String TOKEN_ALIAS_NAME		= "__tokenAlias";
	public static final String APP_ID				= "__appId";
	public static final String APP_NAME				= "__appName";
	public static final String SERVICE_ID			= "__serviceId";
	public static final String SERVICE_NAME			= "__serviceName";
	public static final String SERVICE_ADDR			= "__serviceAddr";
	public static final String ORG_NAME				= "__orgName";
	public static final String OWNER_NAME			= "__ownerName";
	public static final String TOKEN				= "token";
	public static final String APP_CODE				= "__appCode";
	public static final String CURRENT_USER			= "__currentUser";
	public static final String MDC_REQUEST_ID_KEY	= "requestId";
	public static final String MDC_CLIENT_ID_KEY	= "clientId";
	public static final String MDC_SESSION_ID_KEY	= "sessionId";
	public static final String MDC_APP_CODE_KEY		= "appCode";
	public static final String MDC_APP_ID_KEY		= "appId";
	public static final String MDC_APP_NAME_KEY		= "appName";
	public static final String MDC_SERVICE_ID_KEY	= "serviceId";
	public static final String MDC_SERVICE_NAME_KEY	= "serviceName";
	public static final String MDC_SERVICE_ADDR_KEY	= "serviceAddr";
	public static final String MDC_ORG_NAME_KEY		= "orgName";
	public static final String MDC_OWNER_NAME_KEY	= "ownerName";
	public static final String MDC_TOKEN_KEY		= "token";
	public static final String MDC_TOKEN_ALIAS_KEY	= "tokenAlias";

	public static final String PROP_SERVICE_ID		= "app.id";
	public static final String PROP_SERVICE_NAME	= "app.name";
	
	public static final boolean HTTP_ONLY_COOKIE	= false;
	
	public static final String GET		= "GET";
	public static final String PUT		= "PUT";
	public static final String POST		= "POST";
	public static final String DELETE	= "DELETE";
	public static final String HEAD		= "HEAD";
	public static final String PATCH	= "PATCH";
	public static final String OPTIONS	= "OPTIONS";
	public static final String TRACE	= "TRACE";
	public static final String CONNECT	= "CONNECT";

	public static final String REQUEST_FORMAT_JSON		= "json";
	public static final String REQUEST_FORMAT_FORM		= "form";
	public static final String DEFAULT_REQUEST_CHARSET	= "UTF-8";
	public static final String DEFAULT_REQUEST_FORMAT	= REQUEST_FORMAT_JSON;
	public static final String RESPONSE_CONTENT_TYPE	= "application/json;charset=" + DEFAULT_REQUEST_CHARSET.toLowerCase();
	public static final String REQUEST_INFO_HEADER		= "X-Request-Info";
	
	public static final String MONITOR_LOGGER_NAME		= "LMonitor";
	
	public static final Pattern DOMAIN_PATTERN	= Pattern.compile("[0-9a-zA-Z]+((\\.com\\.cn)|(\\.com)|(\\.cn)|(\\.net)|(\\.org)|(\\.edu))$");
	public static final Pattern SPIDER_PATTERN	= Pattern.compile(".*(Googlebot|Baiduspider|Yahoo\\!\\ Slurp|iaskspider|YodaoBot|msnbot|\\ spider)([\\/\\+\\ \\;]).*", Pattern.CASE_INSENSITIVE);
	
	public static final ThreadLocal<MutableInt> MDC_COUNTER = ThreadLocal.withInitial(() -> new MutableInt(0));

	public static final boolean checkUserAgent(String ua)
	{
		return (GeneralHelper.isStrNotEmpty(ua) && !SPIDER_PATTERN.matcher(ua).matches());
	}
	
	public static final void putMDC(MdcAttr mdcAttr)
	{
		MutableInt counter = MDC_COUNTER.get();
		counter.increment();
		
		if(counter.intValue() > 1)
			return;
		
		mdcAttr.putMdc();
	}

	public static final void clearMDC()
	{
		MutableInt counter = MDC_COUNTER.get();
		
		if(counter.intValue() > 0)
			counter.decrement();
		
		if(counter.intValue() > 0)
			return;

		MDC.clear();
	}
	
	public static final boolean isHasBodyRequest(String method)
	{
		return	GeneralHelper.isStrNotEmpty(method)
				&& (POST.equalsIgnoreCase(method) || PUT.equalsIgnoreCase(method) || PATCH.equals(method));	
	}

	public static final boolean isJsonContentType(String ct)
	{
		if(GeneralHelper.isStrNotEmpty(ct))
			return false;
		
		int index = ct.indexOf('/');
		
		if(index < 0)
			return false;
		
		return ct.regionMatches(true, 0, REQUEST_FORMAT_JSON, index + 1, REQUEST_FORMAT_JSON.length());
	}

	public static final String retriveHostDomain(String host)
	{
		int index = host.indexOf(':');

		if(index != -1)
			host = host.substring(0, index);

		if(!GeneralHelper.isStrIPAddress(host))
		{
			Matcher m = DOMAIN_PATTERN.matcher(host);

			if(m.find())
				host = /*"." +*/ m.group();
		}
		
		return host;
	}
	
	public static final String getConfigProperty(String key)
	{
		String val = System.getProperty(key);
		
		if(GeneralHelper.isStrEmpty(val))
			val = AppConfigHolder.get(key);
		
		return val;		
	}
	
	public static final String getAppId()
	{
		return getConfigProperty(PROP_SERVICE_ID);
	}
	
	public static final String getAppName()
	{
		return getConfigProperty(PROP_SERVICE_NAME);
	}
	
}
