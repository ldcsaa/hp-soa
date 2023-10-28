package io.github.hpsocket.soa.starter.mqtt.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** <b>MQTT 属性定义类</b><p> 
 * 参考配置文件说明
 */
@Component("soaSecondMqttProperties")
@ConfigurationProperties(prefix = "mqtt-second")
@ConditionalOnExpression("'${mqtt-second.serverURIs:}' != '' || '${mqtt-second.server-uris:}' != ''")
public class SoaSecondMqttProperties extends SoaMqttProperties
{

}
