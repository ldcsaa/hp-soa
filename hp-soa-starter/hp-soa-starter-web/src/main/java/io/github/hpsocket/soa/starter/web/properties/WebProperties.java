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
	private AppProperties app = new AppProperties();
	@NestedConfigurationProperty
	private AsyncProperties async = new AsyncProperties();
	@NestedConfigurationProperty
	private CookieProperties cookie = new CookieProperties();
	@NestedConfigurationProperty
	private CorsProperties cors = new CorsProperties();	
	@NestedConfigurationProperty
	private ProxyProperties proxy = new ProxyProperties();	
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
		private boolean readOnly;
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
	public static class CookieProperties
	{
		private int maxAge = WebServerHelper.DEFAULT_COOKIE_MAX_AGE;
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
	public boolean isReadOnly()
	{
		return app.isReadOnly();
	}

	@Override
	public int getCookieMaxAge()
	{
		return cookie.getMaxAge();
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
