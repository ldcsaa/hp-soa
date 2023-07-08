
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
        		/*
                .components(new Components()
        		        .addHeaders(HEADER_REQUEST_INFO, new Header().$ref("string").required(false).example("appCode=; appId=; token=; groupId=; groupType="))
        		        .addHeaders(HEADER_APP_CODE, new Header().$ref("string").required(false).example("3000"))
        		        .addHeaders(HEADER_TOKEN, new Header().$ref("string").required(false).example("2d2fc1d30cffa1cf185cfeed9037b658"))
        		        .addHeaders(HEADER_GROUP_ID, new Header().$ref("long").required(false).example("123456")))
        		*/
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

    /*
    @Override
    @SuppressWarnings("unchecked")
    public void addInterceptors(InterceptorRegistry registry)
    {
        try
        {
            Field registrationsField = FieldUtils.getField(InterceptorRegistry.class, "registrations", true);
            List<InterceptorRegistration> registrations = (List<InterceptorRegistration>) ReflectionUtils.getField(registrationsField, registry);
            if (registrations != null)
            {
                for (InterceptorRegistration interceptorRegistration : registrations) {
                    interceptorRegistration.excludePathPatterns("/springdoc** / **");
            	}
        	}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    */
	
	/*
	@Value("${swagger.config.basePackage:com.lcb.hapi}")
	private String basePackage;
	@Value("${swagger.config.groupName:}")
	private String groupName;
	@Value("${swagger.config.apiTitle:}")
	private String apiTitle;
	@Value("${swagger.config.apiDesc:}")
	private String apiDesc;
	@Value("${swagger.config.apiVersion:}")
	private String apiVersion;

	@Bean
	public Docket createRestApi()
	{
		List<RequestParameter> headers = new ArrayList<RequestParameter>();
		org.springdoc.webmvc.ui.SwaggerConfig
		org.springdoc.core.properties.SwaggerUiConfigProperties
		org.springdoc.core.properties.SwaggerUiConfigParameters
		org.springdoc.core.properties.SwaggerUiOAuthProperties
		org.springdoc.core.configuration.SpringDocUIConfiguration
		headers.add(header(HEADER_REQUEST_INFO, "string", false, "appCode=; appId=; token=; groupId=; groupType="));
		headers.add(header(HEADER_APP_CODE, "string", false, "3000"));
		headers.add(header(HEADER_GROUP_ID, "long", false, "123456"));
		headers.add(header(HEADER_TOKEN, "string", false, "2d2fc1d30cffa1cf185cfeed9037b658"));
				
		return new Docket(DocumentationType.SWAGGER_2)
						.apiInfo(apiInfo())
						.groupName(groupName)
						.genericModelSubstitutes(DeferredResult.class)
						.globalRequestParameters(headers)
						.useDefaultResponseMessages(false)
						.forCodeGeneration(false)
						.enable(true)
						.select()
							.apis(RequestHandlerSelectors.basePackage(basePackage))
							.paths(PathSelectors.any())
							.build();
	}

	private RequestParameter header(String name, String type, boolean required, String defaultValue)
	{
		RequestParameterBuilder param = new RequestParameterBuilder();
		
		return	param.name(name)
				.modelRef(new ModelRef(type))
				.parameterType("header")
				.defaultValue(defaultValue)
				.required(required)
				.build();
	}

	private ApiInfo apiInfo()
	{
		return new ApiInfoBuilder()
						.title(apiTitle)
						.version(apiVersion)
						.description(apiDesc)
						.build();
	}

	@Bean
	UiConfiguration uiConfig()
	{
		return UiConfigurationBuilder.builder()
						.build();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		registry.addResourceHandler("/csrf").addResourceLocations("classpath:/META-INF/resources/csrf/");
		registry.addResourceHandler("/").addResourceLocations("classpath:/META-INF/resources/");
	}
	*/
}
