package io.github.hpsocket.soa.starter.web.properties;

import io.github.hpsocket.soa.framework.web.propertries.IServletPathsPropertries;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties
public class SecurityProperties implements IServletPathsPropertries
{
    //public static final String ANY_PATH_PATTERN = "/**";

    @Value("${server.servlet.context-path:/}")
    private String servletContextPath;
    @Value("${spring.mvc.servlet.path:/}")
    private String springMvcServletPath;
    @Value("${management.endpoints.web.base-path:/actuator}")
    private String managementEndpointsBasePath;
    @Value ("${springdoc.api-docs.path:/v3/api-docs}")
    private String springdocApiDocsPath;
    @Value ("${springdoc.swagger-ui.path:/swagger-ui}")
    private String springdocSwaggerUiPath;

}
