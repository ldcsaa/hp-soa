package io.github.hpsocket.demo.mqtt.controller.impl;

import java.nio.charset.Charset;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.hpsocket.demo.mqtt.contract.req.PublishRequest;
import io.github.hpsocket.demo.mqtt.contract.resp.PublishResponse;
import io.github.hpsocket.demo.mqtt.controller.PublishController;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.annotation.ReadOnlyGuard;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessagePublisher;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**<b>消息发布控制器</b> */
@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
@ReadOnlyGuard(enabled = false, desc = "just read only - 1 ~")
public class PublishControllerImpl implements PublishController
{
    /** MQTT 消息发布器 */
    @Autowired
    MqttMessagePublisher publisher;
    
    /**
     * <b>发布消息接口</b><p>
     * 
     * @param req 消息发布请求对象
     * @return 消息发布响应对象
     */
    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    //@ReadOnlyGuard(desc = "just read only - 2 ~")
    public Response<PublishResponse> publish(@RequestBody @Valid PublishRequest req)
    {
        log.info("[发布消息请求] -> {}", req);
        
        PublishResponse resp = doPublish(req);
        
        log.info("[发布消息结果] -> {}", resp);
        
        return new Response<>(resp);
    }

    private PublishResponse doPublish(PublishRequest req)
    {
        // 创建 payload
        byte[] payload =req.getMessage().getBytes(Charset.forName("UTF-8"));
        
        try
        {
            IMqttToken token;
            
            if(req.isUseDefaultOptions())
            {
                // 用默认属性发布消息
                MqttMessage msg = publisher.createMqttMessage(payload);
                token = publisher.publish(req.getTopic(), msg);
            }
            else
            {
             // 用指定属性发布消息
                token = publisher.publish(req.getTopic(), payload, req.getQos(), req.isRetained());
            }
            
            // 返回成功结果
            return new PublishResponse(token.getMessageId());
        }
        catch(MqttException e)
        {
            log.error("publish message exception -> {}", e.getMessage(), e);
            
            // 返回失败结果
            return new PublishResponse(e.getReasonCode(), e.getMessage());
        }
    }
    
}
