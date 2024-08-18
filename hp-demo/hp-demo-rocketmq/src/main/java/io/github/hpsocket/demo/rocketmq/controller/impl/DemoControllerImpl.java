package io.github.hpsocket.demo.rocketmq.controller.impl;

import java.time.Duration;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;
import org.apache.rocketmq.client.common.Pair;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.hpsocket.demo.rocketmq.contract.req.CreateOrderReuqest;
import io.github.hpsocket.demo.rocketmq.contract.resp.CreateOrderResponse;
import io.github.hpsocket.demo.rocketmq.controller.DemoController;
import io.github.hpsocket.demo.rocketmq.converter.OrderConverter;
import io.github.hpsocket.demo.rocketmq.entity.Order;
import io.github.hpsocket.demo.rocketmq.listener.TransCheckerListener;
import io.github.hpsocket.demo.rocketmq.template.FifoPruducerTemplate;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
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
    private static final String FIFO_MESSAGE_GROUP = "fifo-message-group-001";
    
    @Value("${rocketmq.simple-consumer.topic}")
    private String normalTopic;
    @Value("${rocketmq-fifo.simple-consumer.topic}")
    private String fifoTopic;
    @Value("${rocketmq-delay.push-consumer.topic}")
    private String delayTopic;
    @Value("${rocketmq-trans.push-consumer.topic}")
    private String transTopic;

    @Autowired
    private SoaRocketMQClientTemplate defaultTemplate;
    
    @Autowired
    //@Qualifier(FifoPruducerTemplate.BEAN_NAME)
    private FifoPruducerTemplate fifoPruducerTemplate;
    
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
        
        return response(event.getMsgId(), receipt.getMessageId(), request.getOrderNumber());
    }

    @Override
    public Response<CreateOrderResponse> fifoCreateOrder(@Valid CreateOrderReuqest request)
    {
        DomainEvent<Order> event = createEvent(request, fifoTopic);
        
        SendReceipt receipt = fifoPruducerTemplate.syncSendFifoMessage(event.getDestination(), event.toMessage(), FIFO_MESSAGE_GROUP);
        
        return response(event.getMsgId(), receipt.getMessageId(), request.getOrderNumber());
    }

    @Override
    public Response<CreateOrderResponse> delayCreateOrder(@Valid CreateOrderReuqest request)
    {
        DomainEvent<Order> event = createEvent(request, delayTopic);
        
        SendReceipt receipt = defaultTemplate.syncSendDelayMessage(event.getDestination(), event.toMessage(), Duration.ofSeconds(10));
        
        return response(event.getMsgId(), receipt.getMessageId(), request.getOrderNumber());
    }

    @Override
    public Response<CreateOrderResponse> transCreateOrder(@Valid CreateOrderReuqest request)
    {
        DomainEvent<Order> event = createEvent(request, transTopic);
        
        Pair<SendReceipt, Transaction> pair = defaultTemplate.sendTransactionMessage(event.getDestination(), event.toMessage());
        SendReceipt receipt = pair.getSendReceipt();
        Transaction transaction = pair.getTransaction();
        
        doInLocalTransaction(transaction, request.getOrderNumber());
        
        return response(event.getMsgId(), receipt.getMessageId(), request.getOrderNumber());
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

    private void doInLocalTransaction(Transaction transaction, String orderNumber)
    {        
        try
        {
            /* 2 - ROLLBACK, 3 - IGNORE, OTHER - COMMIT */
            
            if(orderNumber.endsWith("2"))
                transaction.rollback();
            else if(orderNumber.endsWith("3"))
            {
                char c = orderNumber.charAt(orderNumber.length() - 2);
                
                if(c >= '0' && c <= '4')
                    transCheckerListener.getBizIds().add(orderNumber);
            }
            else
            {
                transaction.commit();
                transCheckerListener.getBizIds().add(orderNumber);
            }
        }
        catch(ClientException e)
        {
            log.error("Do in local transaction exception -> {}", e.getMessage(), e);
            ServiceException.throwServiceException(e);
        }
        
    }

    private Response<CreateOrderResponse> response(String messageId, MessageId internalMessageId, String orderNumber)
    {
        String strInternalMessageId = internalMessageId == null ? null : internalMessageId.toString();
        CreateOrderResponse resp = new CreateOrderResponse(messageId, strInternalMessageId, orderNumber);
        
        log.debug(resp.toString());

        return new Response<>(resp);
    }

}
