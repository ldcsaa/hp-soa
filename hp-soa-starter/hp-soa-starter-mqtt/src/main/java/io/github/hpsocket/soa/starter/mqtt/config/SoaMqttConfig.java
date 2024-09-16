package io.github.hpsocket.soa.starter.mqtt.config;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;

import io.github.hpsocket.soa.framework.web.support.NumberByteArrayConverter;
import io.github.hpsocket.soa.framework.web.support.StringByteArrayConverter;
import io.github.hpsocket.soa.starter.mqtt.consume.aspect.MqttListenerMdcInspector;
import io.github.hpsocket.soa.starter.mqtt.consume.aspect.MqttListenerTracingInspector;
import io.github.hpsocket.soa.starter.mqtt.listener.MqttReadOnlyEventListener;

/** <b>MQTT 公共配置类</b> */
@AutoConfiguration
@Import ({
            StringByteArrayConverter.class,
            NumberByteArrayConverter.class,
            MqttReadOnlyEventListener.class
        })
@ComponentScan("io.github.hpsocket.soa.starter.mqtt.properties")
public class SoaMqttConfig
{
    @Bean
    @ConditionalOnClass(Trace.class)
    MqttListenerTracingInspector mqttListenerTracingInspector()
    {
        return new MqttListenerTracingInspector();
    }    
    
    @Bean
    MqttListenerMdcInspector mqttListenerMdcInspector()
    {
        return new MqttListenerMdcInspector();
    }

}
