
package org.hpsocket.soa.framework.web.server.spring.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import static com.lcb.soa.misc.server.ServerHelper.*;

@Configuration
@EnableSwagger2WebMvc
@ConditionalOnProperty(name="swagger.enable", havingValue="true", matchIfMissing = false)
public class SwaggerConfiguration implements WebMvcConfigurer
{
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
		List<Parameter> headers = new ArrayList<Parameter>();
		
		headers.add(header(REQUEST_INFO_HEADER, "string", false, "appCode=; appId=; token=; groupId=; groupType="));
		headers.add(header(LCB_CLIENT_APP_CODE, "string", false, "302"));
		headers.add(header(LCB_CLIENT_APP_ID, "string", false, ""));
		headers.add(header(LCB_GROUP_ID, "int", false, "-1"));
		headers.add(header(LCB_GROUP_TYPE, "int", false, "0"));
		headers.add(header(LCB_TOKEN, "string", false, "2d2fc1d30cffa1cf185cfeed9037b658"));
				
		return new Docket(DocumentationType.SWAGGER_2)
						.apiInfo(apiInfo())
						.groupName(groupName)
						.genericModelSubstitutes(DeferredResult.class)
						.globalOperationParameters(headers)
						.useDefaultResponseMessages(false)
						.forCodeGeneration(false)
						.enable(true)
						.select()
							.apis(RequestHandlerSelectors.basePackage(basePackage))
							.paths(PathSelectors.any())
							.build();
	}

	private Parameter header(String name, String type, boolean required, String defaultValue)
	{
		ParameterBuilder param = new ParameterBuilder();
		
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
						/*
						.deepLinking(true)
						.displayOperationId(false)
						.defaultModelsExpandDepth(1)
						.defaultModelExpandDepth(1)
						.defaultModelRendering(ModelRendering.EXAMPLE)
						.displayRequestDuration(false)
						.docExpansion(DocExpansion.NONE)
						.filter(false).maxDisplayedTags(null)
						.operationsSorter(OperationsSorter.ALPHA)
						.showExtensions(false)
						.tagsSorter(TagsSorter.ALPHA)
						.validatorUrl(null)
						*/
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
}
