package io.github.hpsocket.demo.infra.cloud.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqProducer;

@AutoConfiguration
@EnableSoaRabbitmqProducer
/* default mybatis mapper scan package -> ${hp.soa.data.mysql.mapper-scan.base-package} */
//@MapperScan("io.github.hpsocket.demo.infra.cloud.mapper")
/* default feign clients scan package -> ${hp.soa.web.cloud.feign-clients.base-package} */
//@EnableFeignClients("io.github.hpsocket.demo.infra.cloud.client")
public class AppConfig
{
    public static final String DOMAIN_NAME              = "demo.user";
    public static final String SAVE_USER_EVENT_NAME     = "saveUser";
    public static final String SAVE_USER_ROUTING_KEY    = "user.save";
    public static final String USER_EXCHANGE            = "EXC_USER";
    
    @Autowired
    private AmqpAdmin rabbitAmqpAdmin;
    
    @Bean
    TopicExchange userTopicExchange()
    {
        return ExchangeBuilder.topicExchange(USER_EXCHANGE).durable(true).admins(rabbitAmqpAdmin).build();
    }
}
