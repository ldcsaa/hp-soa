package io.github.hpsocket.soa.starter.mqtt.support;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** <b>扩展 MQTT Client 对象</b> */
@Slf4j
@Getter
public class ExtMqttClient extends MqttClient
{
    private boolean manualAcks = false;
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, boolean manualAcks) throws MqttException
    {
        super(serverURI, clientId, persistence);
        this.manualAcks = manualAcks;
    }
    
    public void disconnectAndClose()
    {
        if(isConnected())
        {
            try
            {
                log.info("disconnect mqtt client");
                disconnect();
            }
            catch(MqttException e)
            {
                log.error("mqtt client disconnect fail -> {}", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        
        try
        {
            log.info("close mqtt client");
            close();
        }
        catch(MqttException e)
        {
            log.error("mqtt client close fail -> {}", e.getMessage(), e);
        }
    }
    
}

