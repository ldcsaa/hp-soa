
package com.github.hpsocket.soa.starter.web.config;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.filter.HttpMdcFilter;
import com.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import com.github.hpsocket.soa.starter.web.properties.SecurityProperties;
import com.github.hpsocket.soa.starter.web.properties.WebProperties;
import com.github.hpsocket.soa.starter.web.properties.WebProperties.AppProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
public class WebConfig implements WebMvcConfigurer
{
	private final WebProperties webProperties;
	private final SecurityProperties securityProperties;
	
	public WebConfig(WebProperties webProperties, SecurityProperties securityProperties)
	{
		AppProperties app = webProperties.getApp();
		
		if(GeneralHelper.isStrEmpty(app.getId()) || GeneralHelper.isStrEmpty(app.getName()))
			throw new RuntimeException(String.format("({}) init fail -> 'hp.soa.web.app.id' or 'hp.soa.web.app.name' property is empty", WebConfig.class.getSimpleName()));

		this.webProperties = webProperties;
		this.securityProperties = securityProperties;
		
		AppConfigHolder.init(webProperties, webProperties);
	}

	@Bean
	public FilterRegistrationBean<HttpMdcFilter> mdcTracingFilterRegistration()
	{
		FilterRegistrationBean<HttpMdcFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter( new HttpMdcFilter());
		registration.setName(HttpMdcFilter.DISPLAY_NAME);
		registration.addUrlPatterns(HttpMdcFilter.URL_PATTERNS);
		registration.setOrder(HttpMdcFilter.ORDER);
		registration.setEnabled(true);
		
		return registration;
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		String endpointsWebBasePath = securityProperties.getEndpointsWebBasePath();
		
		if(GeneralHelper.isStrEmpty(endpointsWebBasePath) || endpointsWebBasePath.equals("/"))
			http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().permitAll());
		else
		{
			String servletContextPath = securityProperties.getServletContextPath();
			String mvcServletPath = securityProperties.getMvcServletPath();
			
			StringBuilder sb = new StringBuilder();
			
			if(GeneralHelper.isStrNotEmpty(servletContextPath) && !servletContextPath.equals("/"))
				sb.append(servletContextPath);
			if(GeneralHelper.isStrNotEmpty(mvcServletPath) && !mvcServletPath.equals("/"))
				sb.append(mvcServletPath);
			
			sb.append(endpointsWebBasePath);
		
			String managementBasePath = sb.append(SecurityProperties.ANY_PATH_PATTERN).toString();
			
			http
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers(managementBasePath).authenticated()
				.requestMatchers(SecurityProperties.ANY_PATH_PATTERN).permitAll())
			.formLogin(Customizer.withDefaults())
			.httpBasic(Customizer.withDefaults());
		}
		
		http.csrf((csrf) -> csrf.disable());
		
		return http.build();
	}
	
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
       return ((web) -> {});
    }

	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		WebProperties.CorsProperties crosProperties = webProperties.getCors();
		
		CorsRegistration mapping = registry.addMapping(crosProperties.getMapping());
		
		mapping
			.allowedOrigins(crosProperties.getAllowedOrigins())
			.allowedHeaders(crosProperties.getAllowedHeaders())
			.allowedMethods(crosProperties.getAllowedMethods())
			.allowCredentials(crosProperties.isAllowCredentials())
			.maxAge(crosProperties.getMaxAge());
		
		String[] exposedHeaders = crosProperties.getExposedHeaders();
		
		if(exposedHeaders != null && exposedHeaders.length > 0 && !GeneralHelper.isStrEmpty(exposedHeaders[0]))
			mapping.exposedHeaders(exposedHeaders);
	}

}
