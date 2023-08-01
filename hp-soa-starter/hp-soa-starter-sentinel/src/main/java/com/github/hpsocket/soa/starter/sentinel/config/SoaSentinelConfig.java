
package com.github.hpsocket.soa.starter.sentinel.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.starter.sentinel.properties.SoaSentinelProperties;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@ConditionalOnBean(SoaSentinelProperties.class)
public class SoaSentinelConfig
{
	@Bean
	public SentinelResourceAspect sentinelResourceAspect()
	{
		return new SentinelResourceAspect();
	}

	@Bean
	public FilterRegistrationBean<CommonFilter> sentinelFilterRegistration()
	{
		FilterRegistrationBean<CommonFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new SentinelWebFilter());
		registration.setName(SentinelWebFilter.DISPLAY_NAME);
		registration.addUrlPatterns(SentinelWebFilter.URL_PATTERNS);
		registration.setOrder(SentinelWebFilter.ORDER);
		registration.setEnabled(true);
		registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "true");

		return registration;
	}

	@PostConstruct
	public void postConstruct()
	{
		Set<String> suffixSet = new HashSet<>(Arrays.asList(".js", ".css", ".html", ".ico", ".txt", ".md", ".jpg", ".png"));

		WebCallbackManager.setUrlCleaner(url ->
		{
			if(GeneralHelper.isStrEmpty(url))
				return url;
			if(suffixSet.stream().anyMatch(url::endsWith))
				return null;
			
			return url;
		});
	}
	
	public static class SentinelWebFilter extends CommonFilter
	{
		public static final int ORDER			= -50;
		public static final String DISPLAY_NAME	= SentinelWebFilter.class.getSimpleName();
		public static final String URL_PATTERNS	= "/*";

		@Override
		public void init(FilterConfig filterConfig) throws ServletException
		{
			log.info("starting up: ({}) ...", DISPLAY_NAME);
		}
		
		@Override
		public void destroy()
		{
			log.info("shutting down: ({}) OK!", DISPLAY_NAME);
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
		{
			super.doFilter(request, response, chain);
		}
	}

}
