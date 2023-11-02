package io.github.hpsocket.soa.starter.mqtt.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** <b>MQTT 属性定义类</b><p> 
 * 参考配置文件说明
 */
@Component("soaFirstMqttProperties")
@ConfigurationProperties(prefix = "mqtt-first")
@ConditionalOnExpression("'${mqtt-first.serverURIs:}' != '' || '${mqtt-first.server-uris:}' != ''")
public class SoaFirstMqttProperties extends SoaMqttProperties
{

}
