package io.github.hpsocket.demo.mq.producer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqProducer;

@Configuration
/* default mybatis mapper scan package -> ${hp.soa.web.mapper-scan.base-package} */
//@EnableSoaRabbitmqProducer
@MapperScan("io.github.hpsocket.demo.mq.producer.mapper")
public class AppConfig
{
	public static final String DOMAIN_NAME				= "demo.order";
	public static final String CREATE_ORDER_EVENT_NAME	= "createOrder";
	public static final String CREATE_ORDER_ROUTING_KEY	= "order.create.*";
	
	public static final String[] REGION_0_EXCHANGES		= {"EXC_REGION_0", "EXC_REGION_1", "EXC_REGION_2", "EXC_REGION_3"};
}
