package io.github.hpsocket.demo.mq.producer.config;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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
	
	public static final String[] REGION_EXCHANGES		= {"EXC_REGION_0", "EXC_REGION_1", "EXC_REGION_2", "EXC_REGION_3"};
	public static final String[] REGION_QUEUES			= {"QUE_REGION_0", "QUE_REGION_1", "QUE_REGION_2", "QUE_REGION_3"};
	
	@Bean
	TopicExchange region0TopicExchange()
	{
		return new TopicExchange(REGION_EXCHANGES[0], true, false);
	}
	
	@Bean
	TopicExchange region1TopicExchange()
	{
		return new TopicExchange(REGION_EXCHANGES[1], true, false);
	}
	
	@Bean
	TopicExchange region2TopicExchange()
	{
		return new TopicExchange(REGION_EXCHANGES[2], true, false);
	}
	
	@Bean
	TopicExchange region3TopicExchange()
	{
		return new TopicExchange(REGION_EXCHANGES[3], true, false);
	}
	
	/*
	@Bean
	Queue region0Queue()
	{
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-max-length", 1000000);
		arguments.put("x-max-length-bytes", 300485760);
		
		return new Queue(REGION_QUEUES[0], true, false, false, arguments);
	}
	
	@Bean
	Queue region1Queue()
	{
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-max-length", 1000000);
		arguments.put("x-max-length-bytes", 300485760);
		
		return new Queue(REGION_QUEUES[1], true, false, false, arguments);
	}
	
	@Bean
	Queue region2Queue()
	{
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-max-length", 1000000);
		arguments.put("x-max-length-bytes", 300485760);
		
		return new Queue(REGION_QUEUES[2], true, false, false, arguments);
	}
	
	@Bean
	Queue region3Queue()
	{
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-max-length", 1000000);
		arguments.put("x-max-length-bytes", 300485760);
		
		return new Queue(REGION_QUEUES[3], true, false, false, arguments);
	}
	
	@Bean
	Binding region0Binding(@Qualifier("region0Queue") Queue queue, @Qualifier("region0TopicExchange") TopicExchange exchange)
	{
		return BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
	}
	
	@Bean
	Binding region1Binding(@Qualifier("region1Queue") Queue queue, @Qualifier("region1TopicExchange") TopicExchange exchange)
	{
		return BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
	}
	
	@Bean
	Binding region2Binding(@Qualifier("region2Queue") Queue queue, @Qualifier("region2TopicExchange") TopicExchange exchange)
	{
		return BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
	}
	
	@Bean
	Binding region3Binding(@Qualifier("region3Queue") Queue queue, @Qualifier("region3TopicExchange") TopicExchange exchange)
	{
		return BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
	}
	*/
}
