package com.github.hpsocket.soa.starter.web.properties;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import com.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import com.github.hpsocket.soa.framework.web.propertries.IAccessVerificationProperties;
import com.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import com.github.hpsocket.soa.framework.web.propertries.ICookiePropertries;
import com.github.hpsocket.soa.framework.web.support.WebServerHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hp.soa.web")
public class WebProperties implements IAppProperties, ICookiePropertries, IAccessVerificationProperties
{
	@NestedConfigurationProperty
	private AppProperties app = new AppProperties();
	@NestedConfigurationProperty
	private CookieProperties cookie = new CookieProperties();
	@NestedConfigurationProperty
	private CorsProperties cors = new CorsProperties();	
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
	public int getMaxAge()
	{
		return cookie.getMaxAge();
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
}
