package io.github.hpsocket.soa.starter.mqtt.service;

import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttProperties;

/** <b>MQTT 属性定制器接口</b><p>
 * 应用程序 Bean 可以实现该接口，修改 MQTT 属性
 */
@FunctionalInterface
public interface MqttPropertiesCustomizer
{
    void customize(final SoaMqttProperties mqttProperties);
}
