package com.github.hpsocket.soa.starter.web.config;

import com.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import com.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import com.github.hpsocket.soa.framework.web.advice.ControllerResponseAdvice;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import	({
			ControllerRequestAdvice.class,
			ControllerResponseAdvice.class,
			ControllerGlobalExceptionAdvice.class
		})
public class ControllerAdviceConfig
{

}
