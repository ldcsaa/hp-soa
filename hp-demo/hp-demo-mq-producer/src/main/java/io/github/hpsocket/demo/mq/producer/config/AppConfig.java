package io.github.hpsocket.demo.mq.producer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqProducer;

@Configuration
//@EnableSoaRabbitmqProducer
@MapperScan("io.github.hpsocket.demo.mq.producer.mapper")
public class AppConfig
{

}
