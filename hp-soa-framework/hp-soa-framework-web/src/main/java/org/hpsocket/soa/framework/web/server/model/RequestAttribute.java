package org.hpsocket.soa.framework.web.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestAttribute
{
	private String appCode;
	private String appId;
	private String token;
	private String clientId;
	private String requestId;
	private String sessionId;
	private Long organizationId;
	
	private String remoteIp;
	private String requestUri;
	private String requestPath;
	
	private String tokenAlias;
	private String version;
	private String extra;
	
	private transient Long userId;
	private transient Long internalUserId;
	private transient long timestamp;
	
	private Object body;

	public RequestAttribute(String appCode, String appId, String token, String clientId, String requestId, String sessionId)
	{
		this(appCode, appId, token, clientId, requestId, sessionId, null);
	}
	
	public RequestAttribute(String appCode, String appId, String token, String clientId, String requestId, String sessionId, Long organizationId)
	{
		this.appCode = appCode;
		this.appId = appId;
		this.token = token;
		this.clientId = clientId;
		this.requestId = requestId;
		this.sessionId = sessionId;
		this.organizationId = organizationId;
	}
		
}
