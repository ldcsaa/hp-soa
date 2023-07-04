
package org.hpsocket.soa.framework.web.server.spring.config;

import org.hpsocket.soa.framework.web.server.filter.MdcFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lcb.soa.misc.common.util.GeneralHelper;

@Configuration
public class WebConfiguration implements WebMvcConfigurer
{
	@Value("#{'${web.config.filter.mdcTracing.urlPatterns:/*}'.split(',')}")
	private String[] mdcTracingUrlPatterns;
	@Value("${web.config.filter.mdcTracing.order:0}")
	private int mdcTracingOrder;
	@Value("${web.config.filter.mdcTracing.enabled:true}")
	private boolean mdcTracingEnabled;

	/*
	@Value("#{'${web.config.filter.requestAttribute.urlPatterns:/*}'.split(',')}")
	private String[] requestAttributeUrlPatterns;
	@Value("${web.config.filter.requestAttribute.order:5}")
	private int requestAttributeOrder;
	@Value("${web.config.filter.requestAttribute.enabled:true}")
	private boolean requestAttributeEnabled;
	*/

	@Value("${web.config.cors.mapping:/**}")
	private String mapping;
	@Value("#{'${web.config.cors.allowedOrigins:*}'.split(',')}")
	private String[] allowedOrigins;
	@Value("#{'${web.config.cors.allowedHeaders:*}'.split(',')}")
	private String[] allowedHeaders;
	@Value("#{'${web.config.cors.allowedMethods:*}'.split(',')}")
	private String[] allowedMethods;
	@Value("#{'${web.config.cors.exposedHeaders:}'.split(',')}")
	private String[] exposedHeaders;
	@Value("${web.config.cors.allowCredentials:true}")
	private boolean allowCredentials;
	@Value("${web.config.cors.maxAge:3600}")
	private int maxAge;
	
	@Bean
	public FilterRegistrationBean<MdcFilter> mdcTracingFilterRegistration()
	{
		FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new MdcFilter());
		registration.setName(MdcFilter.DISPLAY_NAME);
		registration.addUrlPatterns(mdcTracingUrlPatterns);
		registration.setOrder(mdcTracingOrder);
		registration.setEnabled(mdcTracingEnabled);
		
		return registration;
	}

	/*
	@Bean
	public FilterRegistrationBean requestAttributeFilterRegistration()
	{
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new RequestAttributeFilter());
		registration.setName(RequestAttributeFilter.DISPLAY_NAME);
		registration.addUrlPatterns(requestAttributeUrlPatterns);
		registration.setOrder(requestAttributeOrder);
		registration.setEnabled(requestAttributeEnabled);
		
		return registration;
	}
	*/

	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		CorsRegistration mapping = registry.addMapping(this.mapping);
		
		mapping
			.allowedOrigins(allowedOrigins)
			.allowedHeaders(allowedHeaders)
			.allowedMethods(allowedMethods)
			.allowCredentials(allowCredentials)
			.maxAge(maxAge);
		
		if(exposedHeaders != null && exposedHeaders.length > 0 && !GeneralHelper.isStrEmpty(exposedHeaders[0]))
			mapping.exposedHeaders(exposedHeaders);
	}

}
