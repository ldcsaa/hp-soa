
package io.github.hpsocket.soa.starter.skywalking.config;

import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.web.dubbo.config.SoaDubboConfig;
import io.micrometer.observation.ObservationRegistry;

/** <b>HP-SOA Skywalking Dubbo 配置</b> */
@AutoConfiguration
@ConditionalOnClass({TraceContext.class, SoaDubboConfig.class})
public class SoaSkyWalkingDubboConfig
{
    @Bean
    ApplicationModel applicationModel(ObservationRegistry observationRegistry)
    {
        ApplicationModel applicationModel = ApplicationModel.defaultModel();
        applicationModel.getBeanFactory().registerBean(observationRegistry);
        
        return applicationModel;
    }
}
