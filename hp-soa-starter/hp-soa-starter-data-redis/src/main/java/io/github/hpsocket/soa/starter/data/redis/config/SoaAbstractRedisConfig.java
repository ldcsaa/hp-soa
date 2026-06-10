package io.github.hpsocket.soa.starter.data.redis.config;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonAutoConfigurationCustomizer;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoNotNullRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.template.NumberRedisTemplate;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.misc.RedisURI;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
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
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

public abstract class SoaAbstractRedisConfig
{
    protected static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    protected static final FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
    protected static final GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();

    protected static final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    protected static final GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

    protected static final KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<Object>();
    protected static final KryoNotNullRedisSerializer<Object> kryoNotNullRedisSerializer = new KryoNotNullRedisSerializer<Object>();

    static
    {
        FastJsonConfig config = new FastJsonConfig();
        config.setJSONB(true);
        fastJsonRedisSerializer.setFastJsonConfig(config);
    }
    
    protected final List<RedissonAutoConfigurationCustomizer> redissonAutoConfigurationCustomizers;

    protected final RedissonProperties redissonProperties;

    protected final RedisProperties redisProperties;

    @Autowired
    private ApplicationContext ctx;

    public SoaAbstractRedisConfig(
        ObjectProvider<List<RedissonAutoConfigurationCustomizer>> redissonAutoConfigurationCustomizers,
        ObjectProvider<RedissonProperties> redissonProperties,
        ObjectProvider<RedisProperties> redisProperties)
    {
        this.redissonAutoConfigurationCustomizers = redissonAutoConfigurationCustomizers.getIfUnique();
        this.redissonProperties = redissonProperties.getIfUnique(() -> new RedissonProperties());
        this.redisProperties = redisProperties.getIfUnique(() -> new RedisProperties());
    }
    
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return jsonRedisTemplate(redisConnectionFactory);
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

    public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(fastJsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    public <T> RedisTemplate<String, T> genericJsonRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, T> template = new RedisTemplate<String, T>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(genericFastJsonRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(genericFastJsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    public RedisTemplate<String, Object> jackson2RedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    public <T> RedisTemplate<String, T> genericJackson2RedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, T> template = new RedisTemplate<String, T>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    public <T> RedisTemplate<String, T> kryoRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, T> template = new RedisTemplate<String, T>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(kryoRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(kryoRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    public <T> RedisTemplate<String, T> kryoNotNullRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, T> template = new RedisTemplate<String, T>();

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(kryoNotNullRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(kryoNotNullRedisSerializer);
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
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
        RedisCacheConfiguration redisDefaultCacheConfiguration,
        MapPropertySource redisInitialCacheConfigurations)
    {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(redisDefaultCacheConfiguration)
            .withInitialCacheConfigurations((Map<String, RedisCacheConfiguration>)(Map<?, ?>)redisInitialCacheConfigurations.getSource())
            .allowCreateOnMissingCache(false);

        return builder.build();
    }
    
    public RedisCacheConfiguration redisDefaultCacheConfiguration()
    {
        return createRedisCacheConfiguration(Duration.ofMinutes(10));
    }
    
    public Map<String, RedisCacheConfiguration> redisInitialCacheSourceMap()
    {
        Map<String, RedisCacheConfiguration> cfgs = new LinkedHashMap<>();

        cfgs.put("10s", createRedisCacheConfiguration(Duration.ofSeconds(10)));
        cfgs.put("30s", createRedisCacheConfiguration(Duration.ofSeconds(30)));
        cfgs.put("1m", createRedisCacheConfiguration(Duration.ofMinutes(1)));
        cfgs.put("5m", createRedisCacheConfiguration(Duration.ofMinutes(5)));
        cfgs.put("15m", createRedisCacheConfiguration(Duration.ofMinutes(15)));
        cfgs.put("30m", createRedisCacheConfiguration(Duration.ofMinutes(30)));
        cfgs.put("1h", createRedisCacheConfiguration(Duration.ofHours(1)));
        cfgs.put("3h", createRedisCacheConfiguration(Duration.ofHours(3)));
        cfgs.put("6h", createRedisCacheConfiguration(Duration.ofHours(6)));
        cfgs.put("12h", createRedisCacheConfiguration(Duration.ofHours(12)));
        cfgs.put("1d", createRedisCacheConfiguration(Duration.ofDays(1)));
        cfgs.put("3d", createRedisCacheConfiguration(Duration.ofDays(3)));
        cfgs.put("7d", createRedisCacheConfiguration(Duration.ofDays(7)));
        cfgs.put("15d", createRedisCacheConfiguration(Duration.ofDays(15)));
        cfgs.put("30d", createRedisCacheConfiguration(Duration.ofDays(30)));
        cfgs.put("60d", createRedisCacheConfiguration(Duration.ofDays(60)));
        cfgs.put("90d", createRedisCacheConfiguration(Duration.ofDays(90)));
        cfgs.put("180d", createRedisCacheConfiguration(Duration.ofDays(180)));
        cfgs.put("1y", createRedisCacheConfiguration(Duration.ofDays(365)));
        
        return cfgs;
    }

    @SuppressWarnings("unchecked")
    public MapPropertySource redisInitialCacheConfigurations(Map<String, RedisCacheConfiguration> redisInitialCacheSourceMap)
    {
        MapFactoryBean bean = new MapFactoryBean();
        bean.setSourceMap(redisInitialCacheSourceMap);

        return new MapPropertySource("redisInitialCacheConfigurations", (Map<String, Object>)(Map<?, ?>)redisInitialCacheSourceMap);
    }

    private RedisCacheConfiguration createRedisCacheConfiguration(Duration duration)
    {
        return RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(duration)
            .serializeKeysWith(SerializationPair.fromSerializer(stringRedisSerializer))
            .serializeValuesWith(SerializationPair.fromSerializer(genericFastJsonRedisSerializer));
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

    private boolean hasConnectionDetails() {
        try {
            Class.forName("org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes"})
    public RedissonClient redisson() throws IOException {
        Config config;
        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        Method usernameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getUsername");
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Method connectTimeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getConnectTimeout");
        Method clientNameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getClientName");

        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
        String prefix = getPrefix();

        String username = null;
        int database = redisProperties.getDatabase();
        String password = redisProperties.getPassword();
        boolean isSentinel = false;
        boolean isCluster = false;
        if (hasConnectionDetails()) {
            ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
            RedisConnectionDetails b = provider.getIfAvailable();
            if (b != null) {
                password = b.getPassword();
                username = b.getUsername();

                if (b.getSentinel() != null) {
                    isSentinel = true;
                }
                if (b.getCluster() != null) {
                    isCluster = true;
                }
            }
        }

        Integer timeout = null;
        if (timeoutValue instanceof Duration) {
            timeout = (int) ((Duration) timeoutValue).toMillis();
        } else if (timeoutValue != null){
            timeout = (Integer) timeoutValue;
        }

        Integer connectTimeout = null;
        if (connectTimeoutMethod != null) {
            Object connectTimeoutValue = ReflectionUtils.invokeMethod(connectTimeoutMethod, redisProperties);
            if (connectTimeoutValue != null) {
                connectTimeout = (int) ((Duration) connectTimeoutValue).toMillis();
            }
        } else {
            connectTimeout = timeout;
        }

        String clientName = null;
        if (clientNameMethod != null) {
            clientName = (String) ReflectionUtils.invokeMethod(clientNameMethod, redisProperties);
        }

        if (usernameMethod != null) {
            username = (String) ReflectionUtils.invokeMethod(usernameMethod, redisProperties);
        }

        if (redissonProperties.getConfig() != null) {
            try {
                config = Config.fromYAML(redissonProperties.getConfig());
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't parse config", e);
            }
        } else if (redissonProperties.getFile() != null) {
            try {
                InputStream is = getConfigStream();
                config = Config.fromYAML(is);
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't parse config", e);
            }
        } else if (redisProperties.getSentinel() != null || isSentinel) {
            String[] nodes = {};
            String sentinelMaster = null;

            if (redisProperties.getSentinel() != null) {
                Method nodesMethod = ReflectionUtils.findMethod(RedisProperties.Sentinel.class, "getNodes");
                Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());
                if (nodesValue instanceof String) {
                    nodes = convert(prefix, Arrays.asList(((String) nodesValue).split(",")));
                } else {
                    nodes = convert(prefix, (List<String>) nodesValue);
                }
                sentinelMaster = redisProperties.getSentinel().getMaster();
            }


            String sentinelUsername = null;
            String sentinelPassword = null;
            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getSentinel() != null) {
                    database = b.getSentinel().getDatabase();
                    sentinelMaster = b.getSentinel().getMaster();
                    nodes = convertNodes(prefix, b.getSentinel().getNodes());
                    sentinelUsername = b.getSentinel().getUsername();
                    sentinelPassword = b.getSentinel().getPassword();
                }
            }

            config = new Config()
                    .setUsername(username)
                    .setPassword(password);

            SentinelServersConfig c = config.useSentinelServers()
                    .setMasterName(sentinelMaster)
                    .addSentinelAddress(nodes)
                    .setSentinelPassword(sentinelPassword)
                    .setSentinelUsername(sentinelUsername)
                    .setDatabase(database)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(config);
        } else if ((clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null)
                || isCluster) {

            String[] nodes = {};
            if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
                Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
                Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
                List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

                nodes = convert(prefix, nodesObject);
            }

            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getCluster() != null) {
                    nodes = convertNodes(prefix, b.getCluster().getNodes());
                }
            }

            config = new Config()
                    .setUsername(username)
                    .setPassword(password);
            ClusterServersConfig c = config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(config);
        } else {
            config = new Config()
                    .setUsername(username)
                    .setPassword(password);

            String singleAddr = prefix + redisProperties.getHost() + ":" + redisProperties.getPort();

            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getStandalone() != null) {
                    database = b.getStandalone().getDatabase();
                    singleAddr = prefix + b.getStandalone().getHost() + ":" + b.getStandalone().getPort();
                }
            }

            SingleServerConfig c = config.useSingleServer()
                    .setAddress(singleAddr)
                    .setDatabase(database)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(config);
        }
        if (redissonAutoConfigurationCustomizers != null) {
            for (RedissonAutoConfigurationCustomizer customizer : redissonAutoConfigurationCustomizers) {
                customizer.customize(config);
            }
        }
        return Redisson.create(config);
    }

    private void initSSL(Config config) {
        Method getSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "getSsl");
        if (getSSLMethod == null) {
            return;
        }

        RedisProperties.Ssl ssl = redisProperties.getSsl();
        if (ssl.getBundle() == null) {
            return;
        }

        ObjectProvider<SslBundles> provider = ctx.getBeanProvider(SslBundles.class);
        SslBundles bundles = provider.getIfAvailable();
        if (bundles == null) {
            return;
        }
        SslBundle b = bundles.getBundle(ssl.getBundle());
        if (b == null) {
            return;
        }
        config.setSslCiphers(b.getOptions().getCiphers());
        config.setSslProtocols(b.getOptions().getEnabledProtocols());
        config.setSslTrustManagerFactory(b.getManagers().getTrustManagerFactory());
        config.setSslKeyManagerFactory(b.getManagers().getKeyManagerFactory());
    }

    private String getPrefix() {
        String prefix = RedisURI.REDIS_PROTOCOL;
        Method isSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
        Method getSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "getSsl");
        if (isSSLMethod != null) {
            if ((Boolean) ReflectionUtils.invokeMethod(isSSLMethod, redisProperties)) {
                prefix = RedisURI.REDIS_SSL_PROTOCOL;
            }
        } else if (getSSLMethod != null) {
            Object ss = ReflectionUtils.invokeMethod(getSSLMethod, redisProperties);
            if (ss != null) {
                Method isEnabledMethod = ReflectionUtils.findMethod(ss.getClass(), "isEnabled");
                Boolean enabled = (Boolean) ReflectionUtils.invokeMethod(isEnabledMethod, ss);
                if (enabled) {
                    prefix = RedisURI.REDIS_SSL_PROTOCOL;
                }
            }
        }
        return prefix;
    }

    @SuppressWarnings("IllegalCatch")
    private String[] convertNodes(String prefix, List<?> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        try {
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
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!RedisURI.isValid(node)) {
                nodes.add(prefix + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }

    private InputStream getConfigStream() throws IOException {
        Resource resource = ctx.getResource(redissonProperties.getFile());
        return resource.getInputStream();
    }

}
