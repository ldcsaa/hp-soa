
package org.hpsocket.soa.starter.web.config;

import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.web.filter.MdcFilter;
import org.hpsocket.soa.starter.web.properties.SecurityProperties;
import org.hpsocket.soa.starter.web.properties.WebProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
	private final WebProperties webProperties;
	private final SecurityProperties securityProperties;
	
	public WebConfig(WebProperties webProperties, SecurityProperties securityProperties)
	{
		this.webProperties = webProperties;
		this.securityProperties = securityProperties;
	}

	@Bean
	public FilterRegistrationBean<MdcFilter> mdcTracingFilterRegistration()
	{
		WebProperties.AppProperties appProperties = webProperties.getApp();
		
		MdcFilter mdcFilter = new MdcFilter();
		mdcFilter.setServiceId(appProperties.getId());
		mdcFilter.setServiceName(appProperties.getName());
		mdcFilter.setOrganization(appProperties.getOrganization());
		mdcFilter.setOwner(appProperties.getOwner());
		
		FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(mdcFilter);
		registration.setName(MdcFilter.DISPLAY_NAME);
		registration.addUrlPatterns(MdcFilter.URL_PATTERNS);
		registration.setOrder(MdcFilter.ORDER);
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
