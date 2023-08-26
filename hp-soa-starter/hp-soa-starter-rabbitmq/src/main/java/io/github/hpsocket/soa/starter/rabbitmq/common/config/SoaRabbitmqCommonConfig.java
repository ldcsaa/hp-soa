package io.github.hpsocket.soa.starter.rabbitmq.common.config;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.messaging.converter.MappingFastJsonMessageConverter;

import io.github.hpsocket.soa.framework.web.json.FastJsonExcludePropertyFilter;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

public class SoaRabbitmqCommonConfig
{
	@Bean
	MessageConverter fastJsonMessageConverter()
	{
		MappingFastJsonMessageConverter converter = new MappingFastJsonMessageConverter();
		
    	FastJsonConfig config = converter.getFastJsonConfig();
    	config.setWriterFeatures(WebServerHelper.JSON_SERIAL_FEATURES_NO_NULL_VAL);
    	config.setJSONB(true);

    	return converter;
	}
}
