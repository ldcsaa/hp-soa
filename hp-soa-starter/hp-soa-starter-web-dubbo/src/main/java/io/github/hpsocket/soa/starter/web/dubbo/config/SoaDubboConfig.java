
package io.github.hpsocket.soa.starter.web.dubbo.config;

import io.github.hpsocket.soa.starter.web.dubbo.advice.DubboExceptionAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/** <b>HP-SOA Dubbo 配置</b> */
@AutoConfiguration
@Import(DubboExceptionAdvice.class)
public class SoaDubboConfig
{

}
