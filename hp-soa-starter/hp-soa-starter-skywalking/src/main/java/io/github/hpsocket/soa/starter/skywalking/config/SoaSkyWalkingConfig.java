
package io.github.hpsocket.soa.starter.skywalking.config;

import java.util.List;

import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.skywalking.apm.meter.micrometer.SkywalkingMeterRegistry;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingDefaultTracingHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingMeterHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingReceiverTracingHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingSenderTracingHandler;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.service.TracingContext;

import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;

/** <b>HP-SOA Skywalking 基本配置</b> */
@AutoConfiguration
@ConditionalOnClass(TraceContext.class)
public class SoaSkyWalkingConfig
{
    /** 调用链上下文服务 */
    @Bean
    public TracingContext tracingContext()
    {
        return new TracingContext() {
            @Override
            public String getTraceId()
            {
                return TraceContext.traceId();
            }

            @Override
            public String getSpanId()
            {
                String segmentId = TraceContext.segmentId();

                if(GeneralHelper.isStrEmpty(segmentId))
                    return null;

                return (segmentId + '#' + TraceContext.spanId());
            }
        };
    }

    @Bean
    ObservationRegistry observationRegistry(List<MeterObservationHandler<?>> handlers)
    {
        ObservationRegistry registry = ObservationRegistry.create();

        registry.observationConfig().observationHandler(
            new ObservationHandler.FirstMatchingCompositeObservationHandler(
                new SkywalkingMeterHandler(new SkywalkingMeterRegistry())));
        registry.observationConfig().observationHandler(
            new ObservationHandler.FirstMatchingCompositeObservationHandler(handlers));
        registry.observationConfig().observationHandler(
            new ObservationHandler.FirstMatchingCompositeObservationHandler(
                new SkywalkingSenderTracingHandler(),
                new SkywalkingReceiverTracingHandler(),
                new SkywalkingDefaultTracingHandler()));
        
        return registry;
    }

    @Bean
    ApplicationModel applicationModel(ObservationRegistry observationRegistry)
    {
        ApplicationModel applicationModel = ApplicationModel.defaultModel();
        applicationModel.getBeanFactory().registerBean(observationRegistry);
        
        return applicationModel;
    }
}
