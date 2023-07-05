package org.hpsocket.soa.framework.web.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "springdoc.api-infos")
@ConditionalOnProperty(name="springdoc.api-docs.enabled", havingValue="true", matchIfMissing = false)
public class SwaggerProperties
{
	private String basePackage = "org.hpsocket";
	private String groupName = "HP-Socket";
	private String title;
	private String description;
	private String version;

}
