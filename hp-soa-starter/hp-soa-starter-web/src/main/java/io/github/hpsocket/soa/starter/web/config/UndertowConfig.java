package io.github.hpsocket.soa.starter.web.config;

import io.github.hpsocket.soa.starter.web.properties.UndertowProperties;
import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/** <b>HP-SOA Undertow Server 配置</b> */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnClass({Undertow.class})
@EnableConfigurationProperties({UndertowProperties.class})
public class UndertowConfig
{
    private final UndertowProperties undertowProperties;
    
    @Bean
    @ConditionalOnClass({WebSocketDeploymentInfo.class})
    WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowWebSocketDeploymentInfoCustomizer()
    {
        return new WebServerFactoryCustomizer<UndertowServletWebServerFactory>()
        {
            @Override
            public void customize(UndertowServletWebServerFactory factory)
            {
                factory.addDeploymentInfoCustomizers((deploymentInfo) ->
                {
                    WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo().setBuffers(
                        new DefaultByteBufferPool(
                        undertowProperties.isWsByteBufferPoolDirect(),
                        undertowProperties.getWsByteBufferPoolBufferSize(),
                        undertowProperties.getWsByteBufferPoolMaximumPoolSize(),
                        undertowProperties.getWsByteBufferPoolThreadLocalCacheSize(),
                        undertowProperties.getWsByteBufferPoolLeakDecetionPercent()));
                    
                    deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.class.getName(), webSocketDeploymentInfo);
                });
            }
        };
    }

}
