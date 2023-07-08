package org.hpsocket.soa.starter.web.config;

import org.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import org.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import org.hpsocket.soa.framework.web.advice.ControllerResponseAdvice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import	({
			ControllerRequestAdvice.class,
			ControllerResponseAdvice.class,
			ControllerGlobalExceptionAdvice.class
		})
public class ControllerAdviceConfig
{

}
