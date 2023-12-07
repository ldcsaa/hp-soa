package io.github.hpsocket.soa.framework.logging.gelf.intern.sender;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.Pool;

/**
 * Pool holder for {@link Pool} that keeps track of Jedis pools identified by {@link URI}.
 *
 * This implementation synchronizes {@link #getJedisPool(URI, int)} and {@link Pool#destroy()} calls to avoid lingering
 * resources and acquisition of disposed resources. creation
 *
 * @author Mark Paluch
 */
class RedisPoolHolder {

    private final static RedisPoolHolder INSTANCE = new RedisPoolHolder();

    private final Map<String, Pool<Jedis>> standalonePools = new HashMap<>();

    private final Object mutex = new Object();

    public static RedisPoolHolder getInstance() {
        return INSTANCE;
    }

    public Pool<Jedis> getJedisPool(URI hostURI, int configuredPort) {

        synchronized (mutex) {

            String lowerCasedConnectionString = hostURI.toString().toLowerCase();
            final String cleanConnectionString = hostURI.getFragment() != null ? lowerCasedConnectionString.substring(0,
                    lowerCasedConnectionString.length() - hostURI.getFragment().length()) : lowerCasedConnectionString;

            Pool<Jedis> pool = standalonePools.get(cleanConnectionString);
            
            if (pool != null) {
                ((RefCount)pool).incrementRefCnt();
            }
            else {
                pool = JedisPoolFactory.createJedisPool(cleanConnectionString, hostURI, configuredPort, Protocol.DEFAULT_TIMEOUT);
                standalonePools.put(cleanConnectionString, pool);
            }

            return pool;
        }
    }

    /**
     * Singleton for administration of commonly used jedis pools
     *
     * @author https://github.com/Batigoal/logstash-gelf.git
     * @author Mark Paluch
     */
    private enum JedisPoolFactory {

        STANDALONE {

            @Override
            public String getScheme() {
                return RedisSenderConstants.REDIS_SCHEME;
            }

            /**
             * Create a Jedis Pool for standalone Redis Operations.
             *
             * @param hostURI
             * @param configuredPort
             * @param timeoutMs
             * @return Pool of Jedis
             */
            @Override
            public Pool<Jedis> createPool(String poolName, URI hostURI, int configuredPort, int timeoutMs) {

                String password = (hostURI.getUserInfo() != null) ? hostURI.getUserInfo().split(":", 2)[1] : null;
                int database = Protocol.DEFAULT_DATABASE;
                if (hostURI.getPath() != null && hostURI.getPath().length() > 1) {
                    database = Integer.parseInt(hostURI.getPath().split("/", 2)[1]);
                }

                int port = hostURI.getPort() > 0 ? hostURI.getPort() : configuredPort;
                return INSTANCE.new MyJedisPool(poolName, new JedisPoolConfig(), hostURI.getHost(), port, timeoutMs, password, database);
            }

        },
        
        SENTINEL {
            
            public static final String MASTER_ID = "masterId";

            @Override
            public String getScheme() {
                return RedisSenderConstants.REDIS_SENTINEL_SCHEME;
            }

            /**
             * Create a Jedis Pool for sentinel Redis Operations.
             *
             * @param hostURI
             * @param configuredPort
             * @param timeoutMs
             * @return Pool of Jedis
             */
            @Override
            public Pool<Jedis> createPool(String poolName, URI hostURI, int configuredPort, int timeoutMs) {

                Set<String> sentinels = getSentinels(hostURI);
                String masterName = getMasterName(hostURI);

                // No logging for Jedis Sentinel at all.
                Logger.getLogger(JedisSentinelPool.class.getName()).setLevel(Level.OFF);

                String password = (hostURI.getUserInfo() != null) ? hostURI.getUserInfo().split(":", 2)[1] : null;
                int database = Protocol.DEFAULT_DATABASE;
                if (hostURI.getPath() != null && hostURI.getPath().length() > 1) {
                    database = Integer.parseInt(hostURI.getPath().split("/", 2)[1]);
                }

                return INSTANCE.new MyJedisSentinelPool(poolName, masterName, sentinels, new JedisPoolConfig(), timeoutMs, password, database);
            }

            protected String getMasterName(URI hostURI) {
                String masterName = "master";

                if (hostURI.getQuery() != null) {
                    String[] keyValues = hostURI.getQuery().split("\\&");
                    for (String keyValue : keyValues) {
                        String[] parts = keyValue.split("\\=");
                        if (parts.length != 2) {
                            continue;
                        }

                        if (parts[0].equals(MASTER_ID)) {
                            masterName = parts[1].trim();
                        }
                    }
                }
                return masterName;
            }

            protected Set<String> getSentinels(URI hostURI) {
                Set<String> sentinels = new HashSet<>();

                String[] sentinelHostNames = hostURI.getHost().split("\\,");
                for (String sentinelHostName : sentinelHostNames) {
                    if (sentinelHostName.contains(":")) {
                        sentinels.add(sentinelHostName);
                    } else if (hostURI.getPort() > 0) {
                        sentinels.add(sentinelHostName + ":" + hostURI.getPort());
                    }
                }
                return sentinels;
            }

        };

        public abstract String getScheme();

        abstract Pool<Jedis> createPool(String poolName, URI hostURI, int configuredPort, int timeoutMs);

        public static Pool<Jedis> createJedisPool(String poolName, URI hostURI, int configuredPort, int timeoutMs) {

            for (JedisPoolFactory provider : JedisPoolFactory.values()) {
                if (provider.getScheme().equals(hostURI.getScheme())) {
                    return provider.createPool(poolName, hostURI, configuredPort, timeoutMs);
                }

            }

            throw new IllegalArgumentException("Scheme " + hostURI.getScheme() + " not supported");
        }

    }
    
    private interface RefCount {
        int incrementRefCnt();
        int decrementRefCnt();
    }
    
    private class MyJedisPool extends JedisPool implements RefCount {
        private final String poolName;
        private int refCnt = 1;
        
        MyJedisPool(String poolName, final GenericObjectPoolConfig<Jedis> poolConfig, final String host, int port, int timeout, final String password, final int database) {
            super(poolConfig, host, port, timeout, password, database, null);
            this.poolName = poolName;
        }
        
        @Override
        public int incrementRefCnt() {
            return ++refCnt;
        }
        
        @Override
        public int decrementRefCnt() {
            return --refCnt;
        }
        
        @Override
        public void destroy()
        {
            synchronized (mutex) {
                
                if(decrementRefCnt() == 0) {
                    standalonePools.remove(this.poolName);
                    super.destroy();
                }
            }
        }
    }

    private class MyJedisSentinelPool extends JedisSentinelPool implements RefCount {
        private final String poolName;
        private int refCnt = 1;
        
        MyJedisSentinelPool(String poolName, String masterName, Set<String> sentinels, final GenericObjectPoolConfig<Jedis> poolConfig, int timeout, final String password, final int database) {
            super(masterName, sentinels, poolConfig, timeout, password, database);
            this.poolName = poolName;
        }
        
        @Override
        public int incrementRefCnt() {
            return ++refCnt;
        }
        
        @Override
        public int decrementRefCnt() {
            return --refCnt;
        }
        
        @Override
        public void destroy()
        {
            synchronized (mutex) {
                
                if(decrementRefCnt() == 0) {
                    standalonePools.remove(this.poolName);
                    super.destroy();
                }
            }
        }
    }

}
