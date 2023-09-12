
package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/** <b>HP-SOA Rabbitmq Producer 配置</b> */
@AutoConfiguration
@MapperScan(basePackages = "io.github.hpsocket.soa.starter.rabbitmq.producer.mapper")
public class SoaRabbitmqProducerConfig
{
	
}
