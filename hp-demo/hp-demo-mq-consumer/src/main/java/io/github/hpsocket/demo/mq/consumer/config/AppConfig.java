package io.github.hpsocket.demo.mq.consumer.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqConsumer;

@Configuration
/* enable rabbitmq consumer */
@EnableSoaRabbitmqConsumer
public class AppConfig
{
	public static final String DOMAIN_NAME				= "demo.order";
	public static final String CREATE_ORDER_EVENT_NAME	= "createOrder";
	public static final String CREATE_ORDER_ROUTING_KEY	= "order.create.*";
	
	public static final String QUE_REGION_0				= "QUE_REGION_0";
	public static final String QUE_REGION_1				= "QUE_REGION_1";
	public static final String QUE_REGION_2				= "QUE_REGION_2";
	public static final String QUE_REGION_3				= "QUE_REGION_3";
	
	public static final String STM_REGION_0				= "default-stream";
	public static final String STM_REGION_1				= "first-stream";
	public static final String STM_REGION_2				= "second-stream";
	public static final String STM_REGION_3				= "third-stream";
	
	public static final String[] REGION_EXCHANGES		= {"EXC_REGION_0", "EXC_REGION_1", "EXC_REGION_2", "EXC_REGION_3"};
	public static final String[] REGION_QUEUES			= {QUE_REGION_0, QUE_REGION_1, QUE_REGION_2, QUE_REGION_3};
	public static final String[] REGION_STREAMS			= {STM_REGION_0, STM_REGION_1, STM_REGION_2, STM_REGION_3};

	
	@Autowired
	@Qualifier("defaultAmqpAdmin")
	AmqpAdmin defaultAmqpAdmin;
	@Autowired
	@Qualifier("firstAmqpAdmin")
	AmqpAdmin firstAmqpAdmin;
	@Autowired
	@Qualifier("secondAmqpAdmin")
	AmqpAdmin secondAmqpAdmin;
	@Autowired
	@Qualifier("thirdAmqpAdmin")
	AmqpAdmin thirdAmqpAdmin;
	
	@Bean
	TopicExchange region0TopicExchange()
	{
		return ExchangeBuilder.topicExchange(REGION_EXCHANGES[0]).durable(true).admins(defaultAmqpAdmin).build();
	}
	
	@Bean
	TopicExchange region1TopicExchange()
	{
		return ExchangeBuilder.topicExchange(REGION_EXCHANGES[1]).durable(true).admins(firstAmqpAdmin).build();
	}
	
	@Bean
	TopicExchange region2TopicExchange()
	{
		return ExchangeBuilder.topicExchange(REGION_EXCHANGES[2]).durable(true).admins(secondAmqpAdmin).build();
	}
	
	@Bean
	TopicExchange region3TopicExchange()
	{
		return ExchangeBuilder.topicExchange(REGION_EXCHANGES[3]).durable(true).admins(thirdAmqpAdmin).build();
	}
	
	@Bean
	Queue region0Queue()
	{
		Queue queue = QueueBuilder.durable(REGION_QUEUES[0]).maxLength(1000000).maxLengthBytes(300485760).build();
		queue.setAdminsThatShouldDeclare(defaultAmqpAdmin);
		
		return queue;
	}
	
	@Bean
	Queue region1Queue()
	{
		Queue queue = QueueBuilder.durable(REGION_QUEUES[1]).maxLength(1000000).maxLengthBytes(300485760).build();
		queue.setAdminsThatShouldDeclare(firstAmqpAdmin);
		
		return queue;
	}
	
	@Bean
	Queue region2Queue()
	{
		Queue queue = QueueBuilder.durable(REGION_QUEUES[2]).maxLength(1000000).maxLengthBytes(300485760).build();
		queue.setAdminsThatShouldDeclare(secondAmqpAdmin);
		
		return queue;
	}
	
	@Bean
	Queue region3Queue()
	{
		Queue queue = QueueBuilder.durable(REGION_QUEUES[3]).maxLength(1000000).maxLengthBytes(300485760).build();
		queue.setAdminsThatShouldDeclare(thirdAmqpAdmin);
		
		return queue;
	}
	
	@Bean
	Binding region0Binding(@Qualifier("region0Queue") Queue queue, @Qualifier("region0TopicExchange") TopicExchange exchange)
	{
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
		binding.setAdminsThatShouldDeclare(defaultAmqpAdmin);
		
		return binding;
	}
	
	@Bean
	Binding region1Binding(@Qualifier("region1Queue") Queue queue, @Qualifier("region1TopicExchange") TopicExchange exchange)
	{
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
		binding.setAdminsThatShouldDeclare(firstAmqpAdmin);
		
		return binding;
	}
	
	@Bean
	Binding region2Binding(@Qualifier("region2Queue") Queue queue, @Qualifier("region2TopicExchange") TopicExchange exchange)
	{
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
		binding.setAdminsThatShouldDeclare(secondAmqpAdmin);
		
		return binding;
	}
	
	@Bean
	Binding region3Binding(@Qualifier("region3Queue") Queue queue, @Qualifier("region3TopicExchange") TopicExchange exchange)
	{
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(CREATE_ORDER_ROUTING_KEY);
		binding.setAdminsThatShouldDeclare(thirdAmqpAdmin);
		
		return binding;
	}
}
