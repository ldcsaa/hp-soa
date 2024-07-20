package io.github.hpsocket.demo.mq.producer.controller.impl;

import io.github.hpsocket.demo.mq.producer.config.AppConfig;
import io.github.hpsocket.demo.mq.producer.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.mq.producer.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.demo.mq.producer.controller.DemoController;
import io.github.hpsocket.demo.mq.producer.converter.OrderConverter;
import io.github.hpsocket.demo.mq.producer.entity.Order;
import io.github.hpsocket.demo.mq.producer.sender.StreamSender;
import io.github.hpsocket.demo.mq.producer.service.OrderService;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.stream.codec.WrapperMessageBuilder;

@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class DemoControllerImpl implements DemoController
{
    @Autowired
    private OrderService orderService;
    
    @Autowired
    StreamSender streamSender;
    
    @Autowired
    private OrderConverter orderConverter;
    
    @Autowired
    private RabbitTemplate defaultRabbitTemplate;
    @Autowired
    @Qualifier("secondRabbitStreamTemplate")
    private RabbitStreamTemplate secondRabbitStreamTemplate;
    
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<DemoCreateOrderResponse> createOrder(@RequestBody @Valid DemoCreateOrderReuqest request)
    {
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
        
        Order order = orderService.createOrder(orderConverter.fromRequest(request));
        DemoCreateOrderResponse resp = orderConverter.toResponse(order);
        
        log.debug(resp.toString());

        Response<DemoCreateOrderResponse> response = new Response<>(resp);
        return response;
    }
    
    @Override
    public Response<DemoCreateOrderResponse> sendStream(@Valid DemoCreateOrderReuqest request)
    {
        Order order = streamSender.sendOrder(orderConverter.fromRequest(request));
        DemoCreateOrderResponse resp = orderConverter.toResponse(order);
        
        log.debug(resp.toString());

        Response<DemoCreateOrderResponse> response = new Response<>(resp);
        return response;
    }
    
    @Override
    public Response<Boolean> sendText(@RequestBody @Valid @NotBlank String text)
    {
        MessageBuilder builder = MessageBuilder.withBody(text.getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ));
        builder .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setContentEncoding(WebServerHelper.DEFAULT_CHARSET);
        
        defaultRabbitTemplate.send(AppConfig.EXC_TEXT, "text.any", builder.build());
        
        return new Response<>(Boolean.TRUE);
    }
    
    @Override
    public Response<Boolean> sendStreamText(@RequestBody @Valid @NotBlank String text)
    {
        com.rabbitmq.stream.MessageBuilder builder = new WrapperMessageBuilder();
        builder .addData(text.getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ))
                .properties()
                    .contentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                    .contentEncoding(WebServerHelper.DEFAULT_CHARSET);
        
        secondRabbitStreamTemplate.send(builder.build());
        
        return new Response<>(Boolean.TRUE);
    }

}
