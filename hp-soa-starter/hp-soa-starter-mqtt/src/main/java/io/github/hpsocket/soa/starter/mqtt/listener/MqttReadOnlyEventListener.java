package io.github.hpsocket.soa.starter.mqtt.listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;
import io.github.hpsocket.soa.starter.mqtt.support.ExtMqttClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>{@linkplain ReadOnlyEvent} 应用程序事件处理器</b><br>
 * 当应用程序为只读时，关闭所有消息监听器，不接收任何消息
 */
@Slf4j
@Getter
@Setter
public class MqttReadOnlyEventListener implements ApplicationListener<ReadOnlyEvent>, Ordered
{
    @Autowired
    private List<ExtMqttClient> mqttClients = new ArrayList<>();

    @Override
    public void onApplicationEvent(ReadOnlyEvent event)
    {
        synchronized(MqttReadOnlyEventListener.class)
        {
            boolean readOnly = event.isReadOnly();
            boolean initial  = event.isInitial();
            
            if(!initial)
                log.info("receive read-only switch event (read-only: {}), prepare to {} MqttClients", readOnly, readOnly ? "STOP" : "START");
            else if(readOnly)
                log.info("application is read-only, then STOP MqttClients");
            
            doSwitch(readOnly);
        }
    }
    
    private void doSwitch(boolean readOnly)
    {
        if(GeneralHelper.isNullOrEmpty(mqttClients))
            return;
        
        for(var mqttClient : mqttClients)
        {
            String clientId = mqttClient.getClientId();
            String action = readOnly ? "disconnect" : "connect";
            
            try
            {
                log.info("mqttClient '{}' {}", clientId, action);
                
                if(readOnly && mqttClient.isConnected())
                    mqttClient.disconnect();
                else if(!readOnly && !mqttClient.isConnected())
                    mqttClient.connect();
            }
            catch(MqttException e)
            {
                String msg = String.format("MqttClient '%s' %s fail -> %s", clientId, action, e.getMessage());
                
                log.error(msg, e);
                
                throw new RuntimeException(msg, e);
            }
        }
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
