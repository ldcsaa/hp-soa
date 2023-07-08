package org.hpsocket.soa.framework.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hp.soa.web")
public class WebProperties
{
	@NestedConfigurationProperty
	private AppProperties app = new AppProperties();
	@NestedConfigurationProperty
	private CorsProperties cors = new CorsProperties();	
	
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
