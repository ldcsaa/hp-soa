package io.github.hpsocket.soa.starter.web.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "springdoc.api-infos")
@ConditionalOnProperty(name="springdoc.api-docs.enabled", havingValue="true", matchIfMissing = false)
public class SwaggerProperties
{
	private String groupName = "HP-Socket";
	private String title;
	private String description;
	private String version;

}
