package io.github.hpsocket.demo.bff.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqConsumer;

@AutoConfiguration
@EnableSoaRabbitmqConsumer
/* default feign clients scan package -> ${hp.soa.web.cloud.feign-clients.base-package} */
//@EnableFeignClients("io.github.hpsocket.demo.bff.cloud.client")
public class AppConfig
{

}
