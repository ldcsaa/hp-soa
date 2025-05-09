package io.github.hpsocket.soa.framework.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <b>HTTP 请求属性</b> */
@Getter
@Setter
@NoArgsConstructor
public class RequestAttribute
{
    /** 游客（未登录）用户 ID */
    public static final long GUEST_USER_ID = -1L;
    
    private String appCode;
    private String srcAppCode;
    private String token;
    private String clientId;
    private String requestId;
    private String sessionId;
    private Long groupId;
    
    private String clientAddr;
    private String requestUri;
    private String requestPath;
    private String requestMethod;
    
    private String region;
    private String language;
    private String version;
    private String extra;
    
    private transient Long userId;
    
    private Object body;
    
    public RequestAttribute(String appCode, String srcAppCode, String token, String clientId, String requestId, String sessionId)
    {
        this(appCode, srcAppCode, token, clientId, requestId, sessionId, null);
    }
    
    public RequestAttribute(String appCode, String srcAppCode, String token, String clientId, String requestId, String sessionId, Long groupId)
    {
        this.appCode    = appCode;
        this.srcAppCode = srcAppCode;
        this.token      = token;
        this.clientId   = clientId;
        this.requestId  = requestId;
        this.sessionId  = sessionId;
        this.groupId    = groupId;
    }
}
