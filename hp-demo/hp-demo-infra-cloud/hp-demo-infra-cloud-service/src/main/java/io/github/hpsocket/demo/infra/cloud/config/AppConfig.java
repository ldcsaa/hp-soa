package io.github.hpsocket.demo.infra.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqProducer;

@AutoConfiguration
@EnableSoaRabbitmqProducer
/* default mybatis mapper scan package -> ${hp.soa.data.mysql.mapper-scan.base-package} */
//@MapperScan("io.github.hpsocket.demo.infra.cloud.mapper")
/* default feign clients scan package -> ${hp.soa.web.cloud.feign-clients.base-package} */
//@EnableFeignClients("io.github.hpsocket.demo.infra.cloud.client")
public class AppConfig
{

}
