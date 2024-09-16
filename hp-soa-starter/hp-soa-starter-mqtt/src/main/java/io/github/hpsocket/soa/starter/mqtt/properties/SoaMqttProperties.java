package io.github.hpsocket.soa.starter.mqtt.properties;

import java.util.ArrayList;
import java.util.List;

import javax.net.SocketFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.util.ssl.SSLUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/** <b>MQTT 属性定义类</b><p> 
 * 参考配置文件说明
 */
@Getter
@Setter
public class SoaMqttProperties extends MqttConnectionOptions
{
    public static final String DEFAULT_WILL_TOPIC   = "default-will-topic";
    public static final int RANDOM_CLIENT_ID_LENGTH = 12;
    
    private String clientId;
    
    private long timeToWait = -1;
    private boolean manualAcks = false;
    private String dataDir;
    
    private String sslCaCertPath;
    private String sslClientCertPath;
    private String sslClientKeyPath;
    private String sslKeyPassword;
    
    @NestedConfigurationProperty
    private PublishOptions publish = new PublishOptions();
    
    @NestedConfigurationProperty
    private List<SubscribeOptions> subscribes = new ArrayList<>();
    
    @PostConstruct
    public void postConstruct()
    {
        parseClientId();
        parseSocketFactory();
    }
    
    @Override
    public void setWill(String topic, MqttMessage message)
    {
        super.setWill(topic, message);
        getWillMessage().setMutable(true);
    }
    
    void setWillDestination(String willDestination)
    {
        setWill(willDestination, getWillMessage() != null ? getWillMessage() : new MqttMessage(new byte[0]));
    }
    
    void setWillMessage(MqttMessage message)
    {
        setWill(GeneralHelper.isStrNotEmpty(getWillDestination()) ? getWillDestination() : DEFAULT_WILL_TOPIC, message);
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

    private void parseClientId()
    {
        String clientId = getClientId();
        
        if(GeneralHelper.isStrEmpty(clientId))
            clientId = RandomStringUtils.insecure().next(RANDOM_CLIENT_ID_LENGTH, true, true);
        else
        {
            final String PH_ADDR = "%A";
            final String PH_PID  = "%P";
            final String PH_RAND = "%R";
            
            if(clientId.indexOf(PH_ADDR) >= 0)
                clientId = clientId.replaceAll(PH_ADDR, SystemUtil.getAddress());
            if(clientId.indexOf(PH_PID) >= 0)
                clientId = clientId.replaceAll(PH_PID, SystemUtil.getPid());
            if(clientId.indexOf(PH_RAND) >= 0)
                clientId = clientId.replaceAll(PH_RAND, RandomStringUtils.insecure().next(RANDOM_CLIENT_ID_LENGTH, true, true));
        }
        
        setClientId(clientId);
    }

    private void parseSocketFactory()
    {
        SocketFactory sf = getSocketFactory();
        
        if(sf == null)
        {
            if(GeneralHelper.isStrNotEmpty(getSslCaCertPath()))
            {
                try
                {
                    if( GeneralHelper.isStrNotEmpty(getSslClientCertPath()) &&
                        GeneralHelper.isStrNotEmpty(getSslClientKeyPath()))
                    {
                        sf = SSLUtil.getSocketFactory(  getSslCaCertPath(),
                                                        getSslClientCertPath(),
                                                        getSslClientKeyPath(),
                                                        getSslKeyPassword());
                    }
                    else
                    {
                        sf = SSLUtil.getSingleSocketFactory(getSslCaCertPath());
                    }
                    
                    setSocketFactory(sf);
                }
                catch(Exception e)
                {
                    throw new BeanCreationException("create socket factory fail: " + e.getMessage(), e);
                }
            }
        }
    }
    
}
