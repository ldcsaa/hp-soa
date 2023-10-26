package io.github.hpsocket.soa.starter.mqtt.service;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;

/** <b>MQTT 消息监听器接口</b><p> 
 * 由应用程序 Bean 实现，处理 MQTT 消息。
 */
public interface MqttMessageListener
{
    /**
     * <b>消息到达回调函数</b><p> 
     * 
     * 注意：如果是手工确认，则需要调用 mqttClient 的 {@linkplain IMqttClient#messageArrivedComplete(int, int) messageArrivedComplete(int, int)} 确认消息
     * 
     * @param mqttClient MQTT 客户端对象
     * @param manualAcks 是否手工确认 
     * @param topic 消息主题
     * @param message 消息对象（包含消息属性和内容）
     * 
     */
    void messageArrived(IMqttClient mqttClient, boolean manualAcks, String topic, MqttMessage message) throws Exception;
}
