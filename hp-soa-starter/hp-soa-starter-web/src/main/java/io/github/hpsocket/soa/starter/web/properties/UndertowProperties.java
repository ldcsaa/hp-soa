package io.github.hpsocket.soa.starter.web.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties
public class UndertowProperties
{
    @Value("${server.undertow.websocket.byte-buffer-pool.direct:false}")
    private boolean wsByteBufferPoolDirect;
    @Value("${server.undertow.websocket.byte-buffer-pool.buffer-size:1024}")
    private int wsByteBufferPoolBufferSize;
    @Value("${server.undertow.websocket.byte-buffer-pool.maximum-pool-size:100}")
    private int wsByteBufferPoolMaximumPoolSize;
    @Value("${server.undertow.websocket.byte-buffer-pool.thread-local-cache-size:12}")
    private int wsByteBufferPoolThreadLocalCacheSize;
    @Value("${server.undertow.websocket.byte-buffer-pool.leak-decetion-percent:0}")
    private int wsByteBufferPoolLeakDecetionPercent;

}
