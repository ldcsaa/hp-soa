
package io.github.hpsocket.soa.starter.data.mysql.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/** <b>HP-SOA Mybatis 默认 {@linkplain MapperScan#basePackages()} 配置</b> */
@AutoConfiguration
@MapperScan("${hp.soa.data.mysql.mapper-scan.base-package}")
@ConditionalOnExpression("'${hp.soa.data.mysql.mapper-scan.base-package:}' != ''")
@ConditionalOnProperty(name = "spring.datasource.dynamic.enabled", matchIfMissing = true)
public class SoaMapperScanConfig
{

}
