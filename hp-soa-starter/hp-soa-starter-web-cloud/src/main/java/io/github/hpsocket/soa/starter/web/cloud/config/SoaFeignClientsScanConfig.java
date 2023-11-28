
package io.github.hpsocket.soa.starter.web.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** <b>HP-SOA Spring Cloud Feign 配置</b> */
@AutoConfiguration
@EnableFeignClients("${hp.soa.web.cloud.feign-clients.base-package}")
@ConditionalOnExpression("'${hp.soa.web.cloud.feign-clients.base-package:}' != ''")
public class SoaFeignClientsScanConfig
{

}
