
package com.github.hpsocket.soa.starter.web.config;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.filter.HttpMdcFilter;
import com.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import com.github.hpsocket.soa.framework.web.propertries.IAsyncProperties;
import com.github.hpsocket.soa.framework.web.service.AsyncService;
import com.github.hpsocket.soa.framework.web.service.impl.AsyncServiceImpl;
import com.github.hpsocket.soa.starter.web.properties.SecurityProperties;
import com.github.hpsocket.soa.starter.web.properties.WebProperties;
import com.github.hpsocket.soa.starter.web.properties.WebProperties.AppProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties({WebProperties.class, SecurityProperties.class})
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
		
		AppConfigHolder.init(webProperties, securityProperties);
	}

	@Bean
	public FilterRegistrationBean<HttpMdcFilter> mdcTracingFilterRegistration()
	{
		FilterRegistrationBean<HttpMdcFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new HttpMdcFilter());
		registration.setName(HttpMdcFilter.DISPLAY_NAME);
		registration.addUrlPatterns(HttpMdcFilter.URL_PATTERNS);
		registration.setOrder(HttpMdcFilter.ORDER);
		registration.setEnabled(true);
		
		return registration;
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		String endpointsWebBasePath = securityProperties.getManagementEndpointsBasePath();
		
		if(GeneralHelper.isStrEmpty(endpointsWebBasePath) || endpointsWebBasePath.equals("/"))
			http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().permitAll());
		else
		{
			String servletContextPath = securityProperties.getServletContextPath();
			String mvcServletPath = securityProperties.getSpringMvcServletPath();
			
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
    
    @Bean
    @ConditionalOnProperty(name="hp.soa.web.async.enabled", havingValue="true", matchIfMissing = true)
    public AsyncService asyncService(IAsyncProperties asyncProperties)
    {
    	AsyncServiceImpl service = new AsyncServiceImpl();
    	service.setCorePoolSize(asyncProperties.getCorePoolSize());
    	service.setMaxPoolSize(asyncProperties.getMaxPoolSize());
    	service.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
    	service.setQueueCapacity(asyncProperties.getQueueCapacity());
    	service.setRejectionPolicy(asyncProperties.getRejectionPolicy());
    	service.setAllowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
    	
    	return service;
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
