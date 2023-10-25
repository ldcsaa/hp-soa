package io.github.hpsocket.soa.starter.mqtt.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/** <b>MQTT 属性定义类</b><p> 
 * 参考配置文件说明
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "mqtt")
public class SoaMqttProperties extends MqttConnectionOptions
{
    private String clientId;
    
    private long timeToWait = -1;
    private boolean manualAcks = false;
    private String dataDir;
    
    private String sslCaCertPath;
    private String sslClientCertPath;
    private String sslClientKeyPath;
    private String sslKeyPassword;
    
    private String willDestination;
    @NestedConfigurationProperty
    private MqttMessage willMessage;
    
    @NestedConfigurationProperty
    private PublishOptions publish = new PublishOptions();
    
    @NestedConfigurationProperty
    private List<SubscribeOptions> subscribes = new ArrayList<>();
    
    @PostConstruct
    public void mergeWill()
    {
        if(GeneralHelper.isStrNotEmpty(getWillDestination()) && getWillMessage() != null && getWillMessage().getPayload() != null)
            setWill(getWillDestination(), getWillMessage());
    }
    
    @Getter
    @Setter
    public static class PublishOptions
    {
        private int defaultQos = 1;
        private boolean defaultRetained = false;
        
        @Override
        public String toString()
        {
            return new StringBuilder()
                        .append('(')
                        .append("defaultQos: ").append(defaultQos)
                        .append(", defaultRetained: ").append(defaultRetained)
                        .append(')')
                        .toString();
        }
    }
    
    @Getter
    @Setter
    public static class SubscribeOptions extends MqttSubscription
    {
        private Integer identifier;
        private Integer topicAlias;
        
        public SubscribeOptions()
        {
            super("");
        }
        
        public SubscribeOptions(String topic)
        {
            super(topic);
        }

        public SubscribeOptions(String topic, int qos)
        {
            super(topic, qos);
        }
        
        @Override
        public String toString()
        {
            return new StringBuilder(200)
                        .append('(')
                        .append("topic: ").append(getTopic())
                        .append(", topicAlias: ").append(getTopicAlias())
                        .append(", identifier: ").append(getIdentifier())
                        .append(", qos: ").append(getQos())
                        .append(", noLocal: ").append(isNoLocal())
                        .append(", retainAsPublished: ").append(isRetainAsPublished())
                        .append(", retainHandling: ").append(getRetainHandling())
                        .append(')')
                        .toString();
        }
    }

}
