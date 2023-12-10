package io.github.hpsocket.soa.framework.logging.gelf.intern.sender;

import java.io.IOException;
import java.net.URI;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.Pool;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfSender;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfSenderConfiguration;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfSenderProvider;

/**
 * {@link GelfSenderProvider} to provide {@link GelfREDISSender}.
 *
 * @author https://github.com/Batigoal/logstash-gelf.git
 * @since 1.4
 */
public class RedisGelfSenderProvider implements GelfSenderProvider {

    @Override
    public boolean supports(String host) {
        return host.startsWith(RedisSenderConstants.REDIS_SCHEME + ":")
                || host.startsWith(RedisSenderConstants.REDIS_SENTINEL_SCHEME + ":");
    }

    @Override
    public GelfSender create(GelfSenderConfiguration configuration) throws IOException {

        String graylogHost = configuration.getHost();

        URI hostUri = URI.create(graylogHost);
        int port = hostUri.getPort();
        if (port <= 0) {
            port = configuration.getPort();
        }

        if (port <= 0) {
            port = Protocol.DEFAULT_PORT;
        }

        if (hostUri.getFragment() == null || hostUri.getFragment().trim().equals("")) {
            throw new IllegalArgumentException("Redis URI must specify fragment");
        }

        if (hostUri.getHost() == null) {
            throw new IllegalArgumentException("Redis URI must specify host");
        }

        Pool<Jedis> pool = RedisPoolHolder.getInstance().getJedisPool(hostUri, port);
        return new GelfREDISSender<>(pool, hostUri.getFragment(), configuration.getErrorReporter());
    }
}
