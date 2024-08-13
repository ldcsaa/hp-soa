package io.github.hpsocket.demo.rocketmq.controller.impl;

import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.hpsocket.demo.rocketmq.contract.req.CreateOrderReuqest;
import io.github.hpsocket.demo.rocketmq.contract.resp.CreateOrderResponse;
import io.github.hpsocket.demo.rocketmq.controller.DemoController;
import io.github.hpsocket.demo.rocketmq.converter.OrderConverter;
import io.github.hpsocket.demo.rocketmq.entity.Order;
import io.github.hpsocket.demo.rocketmq.listener.TransCheckerListener;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.starter.rocketmq.producer.entity.DomainEvent;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class DemoControllerImpl implements DemoController
{
    @Value("${rocketmq.producer.topic}")
    private String normalTopic;
    @Value("${rocketmq-fifo.producer.topic}")
    private String fifoTopic;
    @Value("${rocketmq-delay.producer.topic}")
    private String delayTopic;
    @Value("${rocketmq-trans.producer.topic}")
    private String transTopic;
    @Value("${rocketmq-fifo.simple-consumer.consumer-group}")
    private String fifoGroup;

    @Autowired
    private SoaRocketMQClientTemplate defaultTemplate;
    
    @Autowired
    @Qualifier("fifoPruducerTemplate")
    private SoaRocketMQClientTemplate fifoPruducerTemplate;
    
    @Autowired
    TransCheckerListener transCheckerListener;

    @Autowired
    private OrderConverter orderConverter;
    
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<CreateOrderResponse> normalCreateOrder(@RequestBody @Valid CreateOrderReuqest request)
    {
        DomainEvent<Order> event = createEvent(request, normalTopic);
        
        SendReceipt receipt = defaultTemplate.syncSendNormalMessage(event.getDestination(), event.toMessage());
        
        return response(receipt, request.getOrderNumber());
    }

    @Override
    public Response<CreateOrderResponse> fifoCreateOrder(@Valid CreateOrderReuqest request)
    {
        DomainEvent<Order> event = createEvent(request, fifoTopic);
        
        SendReceipt receipt = fifoPruducerTemplate.syncSendFifoMessage(event.getDestination(), event.toMessage(), fifoGroup);
        
        return response(receipt, request.getOrderNumber());
    }

    @Override
    public Response<CreateOrderResponse> delayCreateOrder(@Valid CreateOrderReuqest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response<CreateOrderResponse> transCreateOrder(@Valid CreateOrderReuqest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private DomainEvent<Order> createEvent(CreateOrderReuqest request, String topic)
    {
        Order order = orderConverter.fromRequest(request);
        DomainEvent<Order> event = new DomainEvent<Order>(order);
        
        event.setDomainName("order");
        event.setEventName("createOrder");
        event.setTopic(topic);
        event.setTag("testTag");
        event.setKeys("testKeys");
        event.setMsgTimestamp(System.currentTimeMillis());
        event.setCorrelationId(order.getOrderNumber());
        
        return event;
    }

    private Response<CreateOrderResponse> response(SendReceipt receipt, String orderNumber)
    {
        String messageId = receipt.getMessageId().toString();
        CreateOrderResponse resp = new CreateOrderResponse(messageId, orderNumber);
        
        log.debug(resp.toString());

        return new Response<>(resp);
    }

}
