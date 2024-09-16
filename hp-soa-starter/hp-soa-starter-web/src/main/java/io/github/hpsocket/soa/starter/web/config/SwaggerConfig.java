
package io.github.hpsocket.soa.starter.web.config;

import io.github.hpsocket.soa.starter.web.properties.SwaggerProperties;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static io.github.hpsocket.soa.framework.web.support.WebServerHelper.*;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;

/** <b>HP-SOA Web Swagger 配置</b> */
@AutoConfiguration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(name="springdoc.api-docs.enabled", havingValue="true", matchIfMissing = false)
public class SwaggerConfig implements WebMvcConfigurer
{
    private static final String SWAGGER_UI_REDIRECT_PATH = "/swagger-ui";
    
    @Value("${springdoc.api-docs.path:/v3/api-docs}")
    String apiDocPath;
    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    String swaggerUiPath;

    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties)
    {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    OpenAPI springDocOpenAPI()
    {
        return new OpenAPI()
                .info(new Info()
                    .title(swaggerProperties.getTitle())
                    .description(swaggerProperties.getDescription())
                    .version(swaggerProperties.getVersion())
                )
                .components(new Components()
                    
                )
                .externalDocs(new ExternalDocumentation()
                    .description("SpringDoc Documentation")
                    .url("https://springdoc.org")
                );
    }

    @Bean
    @ConditionalOnMissingBean(OpenApiCustomizer.class)
    OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser()
    {
        return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> operation
                    .addParametersItem(new HeaderParameter().name(HEADER_REQUEST_INFO).required(false).example("X-App-Code=; X-Token=; X-Group-Id=; X-Request-Id=; X-Client-Id=; X-Session-Id=; X-Src-App-Code=; X-Region=; X-Language=; X-Version=; X-Extra="))
                    .addParametersItem(new HeaderParameter().name(HEADER_APP_CODE).required(false).example("100"))
                    .addParametersItem(new HeaderParameter().name(HEADER_TOKEN).required(false).example("2d2fc1d30cffa1cf185cfeed9037b658"))
                    .addParametersItem(new HeaderParameter().name(HEADER_GROUP_ID).required(false).example("123456"))
                    .addParametersItem(new HeaderParameter().name(HEADER_REQUEST_ID).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_CLIENT_ID).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_SESSION_ID).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_SRC_APP_CODE).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_REGION).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_LANGUAGE).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_VERSION).required(false).example(""))
                    .addParametersItem(new HeaderParameter().name(HEADER_EXTRA).required(false).example(""))
                );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void addInterceptors(InterceptorRegistry registry)
    {
        try
        {
            Field registrationsField = FieldUtils.getField(InterceptorRegistry.class, "registrations", true);
            List<InterceptorRegistration> registrations = (List<InterceptorRegistration>)ReflectionUtils.getField(registrationsField, registry);

            if(registrations != null)
            {
                for(InterceptorRegistration interceptorRegistration : registrations)
                {
                    interceptorRegistration
                    .excludePathPatterns(SWAGGER_UI_REDIRECT_PATH + "/**")
                    .excludePathPatterns(swaggerUiPath)
                    .excludePathPatterns(apiDocPath)
                    .excludePathPatterns(apiDocPath + "/**");
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
