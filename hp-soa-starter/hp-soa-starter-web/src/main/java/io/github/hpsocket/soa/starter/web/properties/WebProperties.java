package io.github.hpsocket.soa.starter.web.properties;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.propertries.IAccessVerificationProperties;
import io.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import io.github.hpsocket.soa.framework.web.propertries.IAsyncProperties;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "hp.soa.web")
public class WebProperties implements IAppProperties, IAsyncProperties, IAccessVerificationProperties
{
    @NestedConfigurationProperty
    private AppProperties app       = new AppProperties();
    @NestedConfigurationProperty
    private AsyncProperties async   = new AsyncProperties();
    @NestedConfigurationProperty
    private HttpProperties http     = new HttpProperties();
    @NestedConfigurationProperty
    private ProxyProperties proxy   = new ProxyProperties();    
    @NestedConfigurationProperty
    private AccessVerificationProperties accessVerification = new AccessVerificationProperties();
    
    @Getter
    @Setter
    public static class AppProperties
    {
        private String id;
        private String name;
        private String version;
        private String organization;
        private String owner;
    }
    
    @Getter
    @Setter
    public static class AsyncProperties
    {
        private boolean enabled = true;
        private int corePoolSize = 4;
        private int maxPoolSize = 16;
        private int keepAliveSeconds = 30;
        private int queueCapacity = 2000;
        private String rejectionPolicy = "CALLER_RUNS";
        private boolean allowCoreThreadTimeOut = true;
    }
    
    @Getter
    @Setter
    public static class HttpProperties
    {
        public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
        
        private String dateTimeFormat   = DEFAULT_DATE_TIME_FORMAT;
        @NestedConfigurationProperty
        private CookieProperties cookie = new CookieProperties();
        @NestedConfigurationProperty
        private CorsProperties cors     = new CorsProperties();    
        
        @Getter
        @Setter
        public static class CookieProperties
        {
            private int maxAge       = WebServerHelper.DEFAULT_COOKIE_MAX_AGE;
            private boolean httpOnly = WebServerHelper.DEFAULT_COOKIE_HTTP_ONLY;
            private boolean secure   = WebServerHelper.DEFAULT_COOKIE_SECURE;
            private String sameSite  = WebServerHelper.DEFAULT_COOKIE_SAME_SITE;
            
            public void setSameSite(String sameSite)
            {
                if(GeneralHelper.isStrNotEmpty(sameSite)
                    && !WebServerHelper.COOKIE_SAME_SITE_STRICT.equalsIgnoreCase(sameSite)
                    && !WebServerHelper.COOKIE_SAME_SITE_LAX.equalsIgnoreCase(sameSite)
                    && !WebServerHelper.COOKIE_SAME_SITE_NONE.equalsIgnoreCase(sameSite))
                    throw new RuntimeException(String.format("invalid config value for property 'hp.soa.http.cookie.same-site' -> '%s'", sameSite));
                
                this.sameSite = sameSite;
            }
        }
        
        @Getter
        @Setter
        public static class CorsProperties
        {
            private String mapping = "/**";
            private String[] allowedOrigins = {"*"};
            private String[] allowedHeaders = {"*"};
            private String[] allowedMethods = {"*"};
            private String[] exposedHeaders = {};
            private boolean allowCredentials = true;
            private int maxAge = 3600;
        }
        
    }
    
    @Getter
    @Setter
    public static class ProxyProperties
    {
        private boolean enabled = false;
        private String scheme;
        private String host;
        private int port;
        private String userName;
        private String password;
        private String nonProxyHosts;
    }

    public static class AccessVerificationProperties
    {
        @Getter
        @Setter
        private boolean enabled = true;
        
        @Getter
        private AccessVerification.Type defaultAccessPolicyEnum = AccessVerification.Type.MAYBE_LOGIN;

        public void setDefaultAccessPolicy(String defaultAccessPolicy)
        {
            if(GeneralHelper.isStrNotEmpty(defaultAccessPolicy))
            {
                defaultAccessPolicyEnum = GeneralHelper.enumLookup(AccessVerification.Type.class, defaultAccessPolicy, true);
                
                if(defaultAccessPolicyEnum == null)
                    throw new RuntimeException(String.format("invalid config value for property 'hp.soa.web.access.default-accessVerification-policy' -> '%s'", defaultAccessPolicy));
            }
        }
    }
    
    @Override
    public Type getDefaultAccessPolicyEnum()
    {
        return accessVerification.getDefaultAccessPolicyEnum();
    }

    @Override
    public String getId()
    {
        return app.getId();
    }

    @Override
    public String getName()
    {
        return app.getName();
    }

    @Override
    public String getVersion()
    {
        return app.getVersion();
    }

    @Override
    public String getOrganization()
    {
        return app.getOrganization();
    }

    @Override
    public String getOwner()
    {
        return app.getOwner();
    }

    @Override
    public int getCookieMaxAge()
    {
        return http.cookie.getMaxAge();
    }
    
    @Override
    public boolean isCookieSecure()
    {
        return http.cookie.isSecure();
    }
    
    @Override
    public boolean isCookieHttpOnly()
    {
        return http.cookie.isHttpOnly();
    }
    
    @Override
    public String getCookieSameSite()
    {
        return http.cookie.getSameSite();
    }
    
    @Override
    public int getCorePoolSize()
    {
        return async.getCorePoolSize();
    }

    @Override
    public int getMaxPoolSize()
    {
        return async.getMaxPoolSize();
    }

    @Override
    public int getKeepAliveSeconds()
    {
        return async.getKeepAliveSeconds();
    }

    @Override
    public int getQueueCapacity()
    {
        return async.getQueueCapacity();
    }

    @Override
    public String getRejectionPolicy()
    {
        return async.getRejectionPolicy();
    }

    @Override
    public boolean isAllowCoreThreadTimeOut()
    {
        return async.isAllowCoreThreadTimeOut();
    }

}
