package io.github.hpsocket.soa.starter.mqtt.consume.aspect;

import java.util.List;

import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.springframework.util.StopWatch;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.AspectHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.mqtt.util.MqttConstant;
import lombok.extern.slf4j.Slf4j;

/** <b>Mqtt Listener MDC 拦截器</b><br>
 * 用于注入 MDC 调用链跟踪信息
 */
@Slf4j
@Aspect
@Order(MqttListenerMdcInspector.ORDER)
public class MqttListenerMdcInspector
{
    public static final int ORDER               = 0;
    public static final String POINTCUT_PATTERN = "(execution ("
                                                +   "public void *.messageArrived("
                                                + "     org.eclipse.paho.mqttv5.client.IMqttClient+, "
                                                + "     boolean, "
                                                + "     java.lang.String, "
                                                + "     org.eclipse.paho.mqttv5.common.MqttMessage+"
                                                + "))"
                                                + "&& target(io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener)"
                                                + ")"
                                                ;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MdcAttr mdcAttr = WebServerHelper.createMdcAttr();

        String messageId         = null;
        String internalMessageId = null;
        String sourceRequestId   = null;
        
        String topic    = AspectHelper.findFirstArgByType(joinPoint, String.class);
        MqttMessage msg = AspectHelper.findFirstArgByType(joinPoint, MqttMessage.class);
        
        if(msg != null)
        {
            int msgId = msg.getId();
            MqttProperties properties = msg.getProperties();
            
            if(msgId != 0) internalMessageId = String.valueOf(msgId);
            
            if(properties != null)
            {
                List<UserProperty> userProps = properties.getUserProperties();
                
                if(GeneralHelper.isNotNullOrEmpty(userProps))
                {
                    for(UserProperty p : userProps)
                    {
                        String key = p.getKey();
                        String val = p.getValue();
                        
                        if(MqttConstant.HEADER_MSG_ID.equalsIgnoreCase(key))
                            messageId = val;
                        else if(MqttConstant.HEADER_SOURCE_REQUEST_ID.equalsIgnoreCase(key))
                            sourceRequestId = val;
                        
                        if(GeneralHelper.isStrNotEmpty(messageId) && GeneralHelper.isStrNotEmpty(sourceRequestId))
                            break;
                    }
                }
           }
            
            if(GeneralHelper.isStrEmpty(messageId))
                messageId = internalMessageId;
            
            if(GeneralHelper.isStrNotEmpty(messageId))
                mdcAttr.setMessageId(messageId);
            if(GeneralHelper.isStrNotEmpty(internalMessageId))
                mdcAttr.setInternalMessageId(internalMessageId);
            if(GeneralHelper.isStrNotEmpty(sourceRequestId))
                mdcAttr.setSourceRequestId(sourceRequestId);
        }
        
        mdcAttr.putMdc();
        
        StopWatch sw = null;
        
        try
        {
            if(log.isTraceEnabled())
            {
                log.trace("start process message -> (topic: {}, messageId: {}, sourceRequestId: {})"
                    , topic, messageId, sourceRequestId);
                
                sw = new StopWatch();
                sw.start();
            }
            
            return joinPoint.proceed();
        }
        finally
        {
            
            if(log.isTraceEnabled())
            {
                sw.stop();

                log.trace("end process message -> (topic: {}, messageId: {}, sourceRequestId: {}, costTime: {})"
                    , topic, messageId, sourceRequestId, sw.getLastTaskTimeMillis());
            }

            mdcAttr.removeMdc();
        }
    }
}
