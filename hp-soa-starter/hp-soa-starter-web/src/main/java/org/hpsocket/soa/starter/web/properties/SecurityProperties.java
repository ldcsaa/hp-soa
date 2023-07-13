package org.hpsocket.soa.starter.web.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class SecurityProperties
{
	public static final String ANY_PATH_PATTERN = "/**";

	@Value("${server.servlet.context-path:}")
	private String servletContextPath;
	@Value("${spring.mvc.servlet.path:}")
	private String mvcServletPath;
	@Value("${management.endpoints.web.base-path:/__admin}")
	private String endpointsWebBasePath;

}
