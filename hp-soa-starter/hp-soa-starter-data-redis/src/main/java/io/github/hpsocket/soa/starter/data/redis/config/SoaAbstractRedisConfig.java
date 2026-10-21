package io.github.hpsocket.soa.starter.data.redis.config;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.starter.data.redis.constant.CacheConstant;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonAutoConfigurationCustomizer;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoNotNullRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.template.NumberRedisTemplate;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.*;
import org.redisson.misc.RedisURI;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.time.Duration;
import java.util.*;

public abstract class SoaAbstractRedisConfig
{
    public static final String[] EMPTY = {};

    protected static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    protected static final FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
    protected static final GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();

    protected static final FastJsonRedisSerializer<Object> fastJsonBRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
    protected static final GenericFastJsonRedisSerializer genericFastJsonBRedisSerializer = new GenericFastJsonRedisSerializer(true);

    protected static final JacksonJsonRedisSerializer<Object> jackson2JsonRedisSerializer = new JacksonJsonRedisSerializer<>(Object.class);
    protected static final GenericJacksonJsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJacksonJsonRedisSerializer(JsonMapper.shared());

    protected static final KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<Object>();
    protected static final KryoNotNullRedisSerializer<Object> kryoNotNullRedisSerializer = new KryoNotNullRedisSerializer<Object>();

    static
    {
        FastJsonConfig config = new FastJsonConfig();
        config.setJSONB(true);
        fastJsonBRedisSerializer.setFastJsonConfig(config);
    }

    private final List<RedissonAutoConfigurationCustomizer> redissonAutoConfigurationCustomizers;
    private final RedissonProperties redissonProperties;
    private final DataRedisProperties redisProperties;

    @Autowired
    private ApplicationContext ctx;

    public SoaAbstractRedisConfig(
        ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        ObjectProvider<RedissonProperties> redissonProperties,
        ObjectProvider<DataRedisProperties> redisProperties)
    {
        this.redissonAutoConfigurationCustomizers = redissonAutoConfigurationCustomizers.getIfUnique();
        this.redissonProperties = redissonProperties.getIfUnique(() -> new RedissonProperties());
        this.redisProperties = redisProperties.getIfUnique(() -> new DataRedisProperties());
    }
    
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return fastJsonRedisTemplate(redisConnectionFactory);
    }
    
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        
        return template;
    }

    public NumberRedisTemplate numberRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        NumberRedisTemplate template = new NumberRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        
        return template;
     }

    public RedisTemplate<String, Object> fastJsonRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, fastJsonRedisSerializer);
    }

    public <T> RedisTemplate<String, T> genericFastJsonRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, genericFastJsonRedisSerializer);
    }

    public RedisTemplate<String, Object> fastJsonBRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, fastJsonBRedisSerializer);
    }

    public <T> RedisTemplate<String, T> genericFastJsonBRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, genericFastJsonBRedisSerializer);
    }

    public RedisTemplate<String, Object> jackson2RedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, jackson2JsonRedisSerializer);
    }

    public <T> RedisTemplate<String, T> genericJackson2RedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, genericJackson2JsonRedisSerializer);
    }

    public <T> RedisTemplate<String, T> kryoRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, kryoRedisSerializer);
    }

    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return createRedisTemplate(redisConnectionFactory, kryoNotNullRedisSerializer);
    }

    public static <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer<?> serializer)
    {
        RedisTemplate<String, T> template = new RedisTemplate<String, T>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(serializer);
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    @SuppressWarnings("unchecked")
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                                                                            .<String, Object>newSerializationContext()
                                                                            .key(RedisSerializer.string())
                                                                            .value(fastJsonRedisSerializer)
                                                                            .hashKey(RedisSerializer.string())
                                                                            .hashValue(fastJsonRedisSerializer)
                                                                            .build();
        
        return new ReactiveRedisTemplate<String, Object>(reactiveRedisConnectionFactory, serializationContext);
    }

    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    @SuppressWarnings("unchecked")
    public RedisCacheManager redisCacheManager(
        RedisConnectionFactory redisConnectionFactory,
        RedisCacheConfiguration redisCacheConfiguration,
        MapPropertySource redisInitialCacheConfigurations)
    {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(redisCacheConfiguration)
            .withInitialCacheConfigurations((Map<String, RedisCacheConfiguration>)(Map<?, ?>)redisInitialCacheConfigurations.getSource())
            .allowCreateOnMissingCache(false);

        return builder.build();
    }
    
    public RedisCacheConfiguration redisCacheConfiguration(ObjectProvider<CacheKeyPrefix> cacheKeyPrefix)
    {
        RedisCacheConfiguration cfg = RedisCacheConfiguration
                                        .defaultCacheConfig()
                                        .entryTtl(Duration.ofMinutes(10))
                                        .serializeKeysWith(SerializationPair.fromSerializer(stringRedisSerializer))
                                        .serializeValuesWith(SerializationPair.fromSerializer(genericFastJsonRedisSerializer));

        CacheKeyPrefix prefix = cacheKeyPrefix.getIfUnique();
        if(prefix != null) {
            cfg = cfg.computePrefixWith(prefix);
        }

        return cfg;
    }

    @SuppressWarnings("unchecked")
    public MapPropertySource redisInitialCacheConfigurations(RedisCacheConfiguration redisCacheConfiguration, ObjectProvider<Map<String, Duration>> redisCustomizeCacheMap)
    {
        Map<String, RedisCacheConfiguration> map = new LinkedHashMap<>();

        Arrays.stream(CacheConstant.InitialCache.values()).forEach(c -> map.put(c.getName(), getRedisCacheConfiguration(redisCacheConfiguration, c.getExpire())));
        redisCustomizeCacheMap.ifUnique(caches -> caches.forEach((k, v) -> map.put(k, getRedisCacheConfiguration(redisCacheConfiguration, v))));

        return new MapPropertySource("redisInitialCacheConfigurations", (Map<String, Object>)(Map<?, ?>)map);
    }

    private RedisCacheConfiguration getRedisCacheConfiguration(RedisCacheConfiguration redisCacheConfiguration, Duration duration)
    {
        return redisCacheConfiguration.entryTtl(duration);
    }

    public KeyGenerator keyGenerator()
    {
        return (target, method, params) ->
        {
            StringBuilder sb = new StringBuilder(100)
                                    .append(target.getClass().getName())
                                    .append(':')
                                    .append(method.getName());

            if(params.length > 0)
            {
                for(int i = 0; i < params.length; i++)
                {
                    sb.append(':').append(params[i]);
                }
            }

            String key = sb.toString();
            if(key.length() > 60)
                key = CryptHelper.md5(key);

            return key;
        };
    }
    
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson)
    {
        return new RedissonConnectionFactory(redisson);
    }

    public RedissonReactiveClient redissonReactive(RedissonClient redisson)
    {
        return redisson.reactive();
    }

    public RedissonRxClient redissonRxJava(RedissonClient redisson)
    {
        return redisson.rxJava();
    }

    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes"})
    public RedissonClient redisson() throws IOException {
        Config config;
        String prefix = getPrefix();

        String username = redisProperties.getUsername();
        int database = redisProperties.getDatabase();
        String password = redisProperties.getPassword();
        String clientName = redisProperties.getClientName();

        boolean isSentinel = false;
        boolean isCluster = false;

        ObjectProvider<DataRedisConnectionDetails> provider = ctx.getBeanProvider(DataRedisConnectionDetails.class);
        DataRedisConnectionDetails connectionDetails = provider.getIfAvailable();
        if (connectionDetails != null) {
            password = connectionDetails.getPassword();
            username = connectionDetails.getUsername();
            isSentinel = connectionDetails.getSentinel() != null;
            isCluster = connectionDetails.getCluster() != null;
        }

        if (redissonProperties.getConfig() != null) {
            config = Config.fromYAML(redissonProperties.getConfig());
        } else if (redissonProperties.getFile() != null) {
            try (InputStream is = getConfigStream()) {
                config = Config.fromYAML(is);
            }
        } else if (redisProperties.getSentinel() != null || isSentinel) {
            config = buildSentinelConfig(prefix, username, password, database, clientName, connectionDetails);
        } else if (redisProperties.getCluster() != null || isCluster) {
            config = buildClusterConfig(prefix, username, password, clientName, connectionDetails);
        } else {
            config = buildSingleServerConfig(prefix, username, password, database, clientName, connectionDetails);
        }

        if (redissonAutoConfigurationCustomizers != null) {
            for (RedissonAutoConfigurationCustomizer customizer : redissonAutoConfigurationCustomizers) {
                customizer.customize(config);
            }
        }
        return Redisson.create(config);
    }

    private Config buildSentinelConfig(String prefix, String username, String password, int database,
                                       String clientName, DataRedisConnectionDetails connectionDetails) {
        String[] nodes;
        String sentinelMaster;
        String sentinelUsername = null;
        String sentinelPassword = null;

        if (connectionDetails != null && connectionDetails.getSentinel() != null) {
            DataRedisConnectionDetails.Sentinel sentinel = connectionDetails.getSentinel();
            database = sentinel.getDatabase();
            sentinelMaster = sentinel.getMaster();
            nodes = convertNodes(prefix, sentinel.getNodes());
            sentinelUsername = sentinel.getUsername();
            sentinelPassword = sentinel.getPassword();
        } else {
            DataRedisProperties.Sentinel sentinel = redisProperties.getSentinel();
            if (sentinel != null) {
                nodes = convert(prefix, sentinel.getNodes());
                sentinelMaster = sentinel.getMaster();
            } else {
                nodes = EMPTY;
                sentinelMaster = null;
            }
        }

        Config config = new Config()
                .setUsername(username)
                .setPassword(password);

        SentinelServersConfig c = config.useSentinelServers()
                .setMasterName(sentinelMaster)
                .addSentinelAddress(nodes)
                .setSentinelPassword(sentinelPassword)
                .setSentinelUsername(sentinelUsername)
                .setDatabase(database)
                .setClientName(clientName);

        setTimeouts(c);
        initSSL(config);
        return config;
    }

    private Config buildClusterConfig(String prefix, String username, String password,
                                      String clientName, DataRedisConnectionDetails connectionDetails) {
        String[] nodes;

        if (connectionDetails != null && connectionDetails.getCluster() != null) {
            nodes = convertNodes(prefix, connectionDetails.getCluster().getNodes());
        } else {
            nodes = convert(prefix, redisProperties.getCluster().getNodes());
        }

        Config config = new Config()
                .setUsername(username)
                .setPassword(password);

        ClusterServersConfig c = config.useClusterServers()
                .addNodeAddress(nodes)
                .setClientName(clientName);

        setTimeouts(c);
        initSSL(config);
        return config;
    }

    private Config buildSingleServerConfig(String prefix, String username, String password, int database,
                                           String clientName, DataRedisConnectionDetails connectionDetails) {
        String singleAddr;

        if (connectionDetails != null && connectionDetails.getStandalone() != null) {
            DataRedisConnectionDetails.Standalone standalone = connectionDetails.getStandalone();
            database = standalone.getDatabase();
            singleAddr = prefix + standalone.getHost() + ":" + standalone.getPort();
        } else {
            singleAddr = prefix + redisProperties.getHost() + ":" + redisProperties.getPort();
        }

        Config config = new Config()
                .setUsername(username)
                .setPassword(password);

        SingleServerConfig c = config.useSingleServer()
                .setAddress(singleAddr)
                .setDatabase(database)
                .setClientName(clientName);

        setTimeouts(c);
        initSSL(config);
        return config;
    }

    private void setTimeouts(BaseConfig c) {
        if (redisProperties.getConnectTimeout() != null) {
            c.setConnectTimeout((int) redisProperties.getConnectTimeout().toMillis());
        }
        if (redisProperties.getTimeout() != null) {
            c.setTimeout((int) redisProperties.getTimeout().toMillis());
        }
    }

    private void initSSL(Config config) {
        DataRedisProperties.Ssl ssl = redisProperties.getSsl();
        if (ssl.getBundle() == null) {
            return;
        }

        ObjectProvider<SslBundles> provider = ctx.getBeanProvider(SslBundles.class);
        SslBundles bundles = provider.getIfAvailable();
        if (bundles == null) {
            return;
        }

        SslBundle bundle = bundles.getBundle(ssl.getBundle());
        config.setSslCiphers(bundle.getOptions().getCiphers());
        config.setSslProtocols(bundle.getOptions().getEnabledProtocols());
        config.setSslTrustManagerFactory(bundle.getManagers().getTrustManagerFactory());
        config.setSslKeyManagerFactory(bundle.getManagers().getKeyManagerFactory());
    }

    private String getPrefix() {
        DataRedisProperties.Ssl ssl = redisProperties.getSsl();
        if (ssl.isEnabled()) {
            return RedisURI.REDIS_SSL_PROTOCOL;
        }
        return RedisURI.REDIS_PROTOCOL;
    }

    @SuppressWarnings("IllegalCatch")
    private String[] convertNodes(String prefix, List<?> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        try {
            // fixes JDK 8 record type compilation error
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            for (Object node : nodesObject) {
                MethodType hostType = MethodType.methodType(String.class);
                MethodHandle hostHandle = lookup.findVirtual(node.getClass(), "host", hostType);
                String host = (String) hostHandle.invoke(node);

                MethodType portType = MethodType.methodType(int.class);
                MethodHandle portHandle = lookup.findVirtual(node.getClass(), "port", portType);
                int port = (int) portHandle.invoke(node);

                nodes.add(prefix + host + ":" + port);
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to convert nodes", e);
        }
        return nodes.toArray(new String[0]);
    }

    private String[] convert(String prefix, List<String> nodesObject) {
        if (nodesObject == null) {
            return EMPTY;
        }
        return nodesObject.stream()
                .map(node -> {
                    if (RedisURI.isValid(node)) {
                        return node;
                    }
                    return prefix + node;
                })
                .toArray(String[]::new);
    }

    private InputStream getConfigStream() throws IOException {
        Resource resource = ctx.getResource(redissonProperties.getFile());
        return resource.getInputStream();
    }

}
