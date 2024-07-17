
package io.github.hpsocket.soa.starter.skywalking.config;

import java.util.List;

import org.apache.skywalking.apm.meter.micrometer.SkywalkingMeterRegistry;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingDefaultTracingHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingMeterHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingReceiverTracingHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingSenderTracingHandler;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.service.TracingContext;
import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;

/** <b>HP-SOA Skywalking 基本配置</b> */
@AutoConfiguration(before = ObservationAutoConfiguration.class)
@ConditionalOnClass(TraceContext.class)
public class SoaSkyWalkingConfig
{
    /** 调用链上下文服务 */
    @Bean
    TracingContext tracingContext()
    {
        return new TracingContext()
        {
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
    @Primary
    @ConditionalOnMissingBean
    ObservationPredicate observationPredicate()
    {
        return (name, context) -> 
        {
            if(name.startsWith("spring.security."))
                return false;
            
            /*
            if(context instanceof ServerRequestObservationContext observationContext)
            {
                String uri = observationContext.getCarrier().getRequestURI();
                return !AppConfigHolder.excludedPath(uri);
            }
            */
            
            return true;
        };
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    ObservationRegistry observationRegistry(List<MeterObservationHandler<?>> handlers, ObservationPredicate predicate)
    {
        ObservationRegistry registry = ObservationRegistry.create();

        registry.observationConfig()
            .observationHandler(
                new ObservationHandler.FirstMatchingCompositeObservationHandler(
                    new SkywalkingMeterHandler(new SkywalkingMeterRegistry())))
            .observationHandler(
                new ObservationHandler.FirstMatchingCompositeObservationHandler(handlers))
            .observationHandler(
                new ObservationHandler.FirstMatchingCompositeObservationHandler(
                    new SkywalkingSenderTracingHandler(),
                    new SkywalkingReceiverTracingHandler(),
                    new SkywalkingDefaultTracingHandler()))
            .observationPredicate(predicate);
        
        return registry;
    }

}
