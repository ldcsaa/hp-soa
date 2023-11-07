package io.github.hpsocket.demo.mqtt.listener;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import io.github.hpsocket.soa.starter.mqtt.config.SoaSecondMqttConfig;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import lombok.extern.slf4j.Slf4j;

/** <b>MQTT 消息监听器</b><p>
 * 
 * <ol>
 * <li>MQTT 消息监听器声明为 Spring Bean</li>
 * <li>HP-SOA 支持多 MQTT 实例，每个 MQTT 实例的消息监听器需通过 Bean 名称区分：
 *   <ul>
 *     <li>默认 MQTT 实例消息监听器：mqttMessageListener</li>
 *     <li>第一个 MQTT 实例消息监听器：firstMqttMessageListener</li>
 *     <li>第二个 MQTT 实例消息监听器：secondMqttMessageListener</li>
 *     <li>如果应用程序只有唯一一个消息监听器 Bean，则所有 MQTT 实例共享该消息监听器，可以不指定 Bean 名称</li>
 *   </ul>
 * </li>
 * </ol>
 * 
 */
@Slf4j
@Component(SoaSecondMqttConfig.mqttMessageListenerBeanName)
public class DemoMqttMessageListener2 implements MqttMessageListener
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
