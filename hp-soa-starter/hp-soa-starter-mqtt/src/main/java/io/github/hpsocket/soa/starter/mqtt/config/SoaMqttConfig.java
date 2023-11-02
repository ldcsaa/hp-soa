package io.github.hpsocket.soa.starter.mqtt.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.framework.web.support.NumberByteArrayConverter;
import io.github.hpsocket.soa.framework.web.support.StringByteArrayConverter;

/** <b>MQTT 公共配置类</b> */
@AutoConfiguration
@Import ({
            StringByteArrayConverter.class,
            NumberByteArrayConverter.class
        })
@ComponentScan("io.github.hpsocket.soa.starter.mqtt.properties")
public class SoaMqttConfig
{

}
