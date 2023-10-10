package io.github.hpsocket.soa.starter.web.config;

import io.github.hpsocket.soa.framework.web.advice.ControllerGlobalExceptionAdvice;
import io.github.hpsocket.soa.framework.web.advice.ControllerRequestAdvice;
import io.github.hpsocket.soa.framework.web.advice.ControllerResponseAdvice;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/** <b>HP-SOA Web Controller Advice 配置</b> */
@AutoConfiguration
@Import ({
            ControllerRequestAdvice.class,
            ControllerResponseAdvice.class,
            ControllerGlobalExceptionAdvice.class
        })
public class ControllerAdviceConfig
{

}
