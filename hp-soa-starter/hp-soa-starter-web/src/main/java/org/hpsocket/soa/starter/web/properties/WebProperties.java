package org.hpsocket.soa.starter.web.properties;

import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.web.annotation.AccessVerification;
import org.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import org.hpsocket.soa.framework.web.propertries.IAccessVerificationProperties;
import org.hpsocket.soa.framework.web.propertries.ICookiePropertries;
import org.hpsocket.soa.framework.web.support.WebServerHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hp.soa.web")
public class WebProperties implements ICookiePropertries, IAccessVerificationProperties
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
		private String organization;
		private String owner;
	}
	
	@Getter
	@Setter
	public static class CookieProperties implements ICookiePropertries
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

	public static class AccessVerificationProperties implements IAccessVerificationProperties
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
}
