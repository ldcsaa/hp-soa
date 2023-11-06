package io.github.hpsocket.demo.mq.producer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqProducer;

@Configuration
/* enable rabbitmq producer */
@EnableSoaRabbitmqProducer
/* default mybatis mapper scan package -> ${hp.soa.data.mysql.mapper-scan.base-package} */
@MapperScan("io.github.hpsocket.demo.mq.producer.mapper")
public class AppConfig
{
    public static final String DOMAIN_NAME              = "demo.order";
    public static final String CREATE_ORDER_EVENT_NAME  = "createOrder";
    public static final String CREATE_ORDER_ROUTING_KEY = "order.create.*";
    
    public static final String[] REGION_EXCHANGES       = {"EXC_REGION_0", "EXC_REGION_1", "EXC_REGION_2", "EXC_REGION_3"};
    public static final String[] REGION_QUEUES          = {"QUE_REGION_0", "QUE_REGION_1", "QUE_REGION_2", "QUE_REGION_3"};
    
    @Autowired
    @Qualifier("defaultRabbitAmqpAdmin")
    private AmqpAdmin defaultRabbitAmqpAdmin;
    @Autowired
    @Qualifier("firstRabbitAmqpAdmin")
    private AmqpAdmin firstRabbitAmqpAdmin;
    @Autowired
    @Qualifier("secondRabbitAmqpAdmin")
    private AmqpAdmin secondRabbitAmqpAdmin;
    @Autowired
    @Qualifier("thirdRabbitAmqpAdmin")
    private AmqpAdmin thirdRabbitAmqpAdmin;
    
    @Bean
    TopicExchange region0TopicExchange()
    {
        return ExchangeBuilder.topicExchange(REGION_EXCHANGES[0]).durable(true).admins(defaultRabbitAmqpAdmin).build();
    }
    
    @Bean
    TopicExchange region1TopicExchange()
    {
        return ExchangeBuilder.topicExchange(REGION_EXCHANGES[1]).durable(true).admins(firstRabbitAmqpAdmin).build();
    }
    
    @Bean
    TopicExchange region2TopicExchange()
    {
        return ExchangeBuilder.topicExchange(REGION_EXCHANGES[2]).durable(true).admins(secondRabbitAmqpAdmin).build();
    }
    
    @Bean
    TopicExchange region3TopicExchange()
    {
        return ExchangeBuilder.topicExchange(REGION_EXCHANGES[3]).durable(true).admins(thirdRabbitAmqpAdmin).build();
    }
}
