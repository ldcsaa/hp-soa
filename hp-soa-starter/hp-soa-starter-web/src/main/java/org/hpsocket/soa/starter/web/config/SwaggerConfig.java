
package org.hpsocket.soa.starter.web.config;

import org.hpsocket.soa.starter.web.properties.SwaggerProperties;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.hpsocket.soa.framework.web.support.WebServerHelper.HEADER_APP_CODE;
import static org.hpsocket.soa.framework.web.support.WebServerHelper.HEADER_GROUP_ID;
import static org.hpsocket.soa.framework.web.support.WebServerHelper.HEADER_REQUEST_INFO;
import static org.hpsocket.soa.framework.web.support.WebServerHelper.HEADER_TOKEN;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;

@Configuration
@ConditionalOnProperty(name="springdoc.api-docs.enabled", havingValue="true", matchIfMissing = false)
public class SwaggerConfig implements WebMvcConfigurer
{
	private final SwaggerProperties swaggerProperties;

	public SwaggerConfig(SwaggerProperties swaggerProperties)
	{
		this.swaggerProperties = swaggerProperties;
	}
	
    @Bean
    public OpenAPI springDocOpenAPI()
    {
        return new OpenAPI()
                .info(new Info()
                        .title(swaggerProperties.getTitle())
                        .description(swaggerProperties.getDescription())
                        .version(swaggerProperties.getVersion())
                )
                .externalDocs(new ExternalDocumentation()
                        .description("SpringDoc Documentation")
                        .url("https://springdoc.org")
                );
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser()
    {
        return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation ->
                {
                    operation.addParametersItem(new HeaderParameter().name(HEADER_REQUEST_INFO).$ref("string").required(false).example("appCode=; appId=; token=; groupId=; groupType="));
                    operation.addParametersItem(new HeaderParameter().name(HEADER_APP_CODE).$ref("string").required(false).example("3000"));
                    operation.addParametersItem(new HeaderParameter().name(HEADER_TOKEN).$ref("string").required(false).example("2d2fc1d30cffa1cf185cfeed9037b658"));
                    operation.addParametersItem(new HeaderParameter().name(HEADER_GROUP_ID).$ref("long").required(false).example("123456"));
                });
    }
}
