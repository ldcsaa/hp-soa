package io.github.hpsocket.soa.starter.web.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "springdoc.api-infos")
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = false)
public class SwaggerProperties
{
    private String groupName = "HP-SOA";
    private String title;
    private String description;
    private String version;

}
