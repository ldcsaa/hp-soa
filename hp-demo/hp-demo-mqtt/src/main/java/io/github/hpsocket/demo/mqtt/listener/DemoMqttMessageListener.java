package io.github.hpsocket.demo.mqtt.listener;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import lombok.extern.slf4j.Slf4j;

/** <b>MQTT 消息监听器<b> */
@Slf4j
/* 声明为 Spring Bean */
@Component
public class DemoMqttMessageListener implements MqttMessageListener
{
    @Override
    public void messageArrived(IMqttClient mqttClient, boolean manualAcks, String topic, MqttMessage message) throws Exception
    {
        log.info("Message Arrived (topic: {}) -> {}", topic, JSON.toJSONString(message));
        
        /* 异常测试：自动确认会丢失消息 */
        /*
        int i = 2;
        if(i%2 == 0)
            throw new RuntimeException("test exception ~");
        */
        
        if(manualAcks)
        {
            /* 手工确认消息 */
            mqttClient.messageArrivedComplete(message.getId(), message.getQos());
        }
    }

}
