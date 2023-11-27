package io.github.hpsocket.demo.bff.cloud.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.rabbitmq.annotation.EnableSoaRabbitmqConsumer;

@AutoConfiguration
@EnableSoaRabbitmqConsumer
/* default feign clients scan package -> ${hp.soa.web.cloud.feign-clients.base-package} */
//@EnableFeignClients("io.github.hpsocket.demo.bff.cloud.client")
public class AppConfig
{
    public static final String DOMAIN_NAME              = "demo.user";
    public static final String SAVE_USER_EVENT_NAME     = "saveUser";
    public static final String SAVE_USER_ROUTING_KEY    = "user.*";
    public static final String USER_QUEUE               = "QUE_USER";;
    public static final String USER_EXCHANGE            = "EXC_USER";
    
    @Autowired
    private AmqpAdmin rabbitAmqpAdmin;
    
    @Bean
    TopicExchange userTopicExchange()
    {
        return ExchangeBuilder.topicExchange(USER_EXCHANGE).durable(true).admins(rabbitAmqpAdmin).build();
    }

    @Bean
    Queue userQueue()
    {
        Queue queue = QueueBuilder.durable(USER_QUEUE).maxLength(1000000).maxLengthBytes(300485760).build();
        queue.setAdminsThatShouldDeclare(rabbitAmqpAdmin);
        
        return queue;
    }
    
    @Bean
    Binding userBinding(Queue queue, TopicExchange exchange)
    {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(SAVE_USER_ROUTING_KEY);
        binding.setAdminsThatShouldDeclare(rabbitAmqpAdmin);
        
        return binding;
    }
}
