package io.github.hpsocket.soa.starter.mqtt.support;

import java.time.Duration;
import java.util.List;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import com.alibaba.fastjson2.JSON;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttProperties.SubscribeOptions;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>默认 MQTT 事件处理器</b><p>
 * 应用程序 Bean 可以实现该接口，修改默认行为
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultMqttCallback implements MqttCallback
{
    private ExtMqttClient mqttClient;
    private List<SubscribeOptions> subscribes;
    private MqttMessageListener messageListener;
    
    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse)
    {
        log.info("(mqtt callback) disconnected : {}", JSON.toJSONString(disconnectResponse));
    }

    @Override
    public void mqttErrorOccurred(MqttException exception)
    {
        log.error("(mqtt callback) mqttErrorOccurred : {}", exception.toString(), exception);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        if(log.isTraceEnabled())
            log.trace("(mqtt callback) messageArrived : (topic: {}) {}", topic, JSON.toJSONString(message));
        
        if(messageListener != null)
            messageListener.messageArrived(mqttClient, mqttClient.isManualAcks(), topic, message);
    }

    @Override
    public void deliveryComplete(IMqttToken token)
    {
        if(log.isTraceEnabled())
            log.trace("(mqtt callback) deliveryComplete : (messageId: {}, isComplete: {})", token.getMessageId(), token.isComplete());
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties)
    {
        if(log.isTraceEnabled())
            log.trace("(mqtt callback) authPacketArrived : (reasonCode: {}, properties: {})", reasonCode, JSON.toJSONString(properties));
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI)
    {
        log.info("(mqtt callback) connectComplete : (reconnect: {}, serverURI: {})", reconnect, serverURI);
        
        if(GeneralHelper.isNullOrEmpty(subscribes))
            return;

        // 连接/重连成功：执行订阅
        // 订阅异常：一直重试
        
        while(mqttClient.isConnected())
        {
            try
            {
                for(SubscribeOptions subscribe : subscribes)
                {
                    mqttClient.subscribe(subscribe, subscribe.getIdentifier(), subscribe.getTopicAlias());
                    log.info("subscribe topic succ -> {}", subscribe);
                }
                
                break;
            }
            catch(Exception e)
            {
                
                log.error("subscribe topics fail -> {} {}", subscribes, e.getMessage(), e);
                GeneralHelper.waitFor(Duration.ofSeconds(5));
            }
        }         
    }

}
