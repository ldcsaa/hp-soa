package io.github.hpsocket.soa.starter.data.redis.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.BaseConfig;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
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
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;

import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonAutoConfigurationCustomizer;
import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoNotNullRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoRedisSerializer;

public class SoaAbstractRedisConfig
{
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    protected static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    protected static final FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
    protected static final GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer(true);

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
    
    public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        return genericJsonRedisTemplate(redisConnectionFactory);
    }
    
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        StringRedisTemplate template = new StringRedisTemplate();
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
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
    {
        RedisSerializationContext<String, T> serializationContext = (RedisSerializationContext<String, T>)
                                                                    (RedisSerializationContext<?, ?>)
                                                                    RedisSerializationContext
                                                                        .<String, T>newSerializationContext()
                                                                        .key(RedisSerializer.string())
                                                                        .value((RedisSerializer<T>)genericFastJsonRedisSerializer)
                                                                        .hashKey(RedisSerializer.string())
                                                                        .hashValue(genericFastJsonRedisSerializer)
                                                                        .build();
        
        return new ReactiveRedisTemplate<String, T>(reactiveRedisConnectionFactory, serializationContext);
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
        return createRedisCacheConfiguration(3600);
    }
    
    @SuppressWarnings("unchecked")
    public MapPropertySource redisInitialCacheConfigurations()
    {
        Map<String, RedisCacheConfiguration> cfgs = new LinkedHashMap<>();

        cfgs.put("10s", createRedisCacheConfiguration(10));
        cfgs.put("30s", createRedisCacheConfiguration(30));
        cfgs.put("1m", createRedisCacheConfiguration(60));
        cfgs.put("5m", createRedisCacheConfiguration(300));
        cfgs.put("15m", createRedisCacheConfiguration(900));
        cfgs.put("30m", createRedisCacheConfiguration(1800));
        cfgs.put("1h", createRedisCacheConfiguration(3600));
        cfgs.put("3h", createRedisCacheConfiguration(10800));
        cfgs.put("6h", createRedisCacheConfiguration(21600));
        cfgs.put("12h", createRedisCacheConfiguration(43200));
        cfgs.put("1d", createRedisCacheConfiguration(86400));
        cfgs.put("7d", createRedisCacheConfiguration(604800));
        cfgs.put("15d", createRedisCacheConfiguration(1296000));
        cfgs.put("30d", createRedisCacheConfiguration(2592000));
        
        MapFactoryBean bean = new MapFactoryBean();
        bean.setSourceMap(cfgs);

        return new MapPropertySource("redisInitialCacheConfigurations", (Map<String, Object>)(Map<?, ?>)cfgs);
    }

    private RedisCacheConfiguration createRedisCacheConfiguration(long ttlSeconds)
    {
        return RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(ttlSeconds))
            .serializeKeysWith(SerializationPair.fromSerializer(stringRedisSerializer))
            .serializeValuesWith(SerializationPair.fromSerializer(genericFastJsonRedisSerializer));
    }

    public KeyGenerator keyGenerator()
    {
        return new KeyGenerator()
        {
            @Override
            public Object generate(Object target, Method method, Object... params)
            {
                StringBuilder sb1 = new StringBuilder()
                                    .append(target.getClass().getName())
                                    .append(':')
                                    .append(method.getName());
                
                String prefix = sb1.toString();
                
                int length = params.length;
                
                if(length == 0)
                    return prefix;
                
                StringBuilder sb2 = new StringBuilder();
                
                for(int i = 0; i < length; i++)
                {
                    Object obj = params[i];
                    sb2.append(obj == null ? "null" : obj.toString());
                    
                    if(i < length - 1)
                        sb2.append(':');
                }
                
                String key = sb2.toString();
                
                if(key.length() > 40)
                    key = CryptHelper.md5(key);
                
                return prefix + ':' + key;
            }
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

    @SuppressWarnings({ "deprecation", "unchecked"})
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
            timeout = (Integer)timeoutValue;
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
            } catch (IOException e) {
                try {
                    config = Config.fromJSON(redissonProperties.getConfig());
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                    throw new IllegalArgumentException("Can't parse config", e1);
                }
            }
        } else if (redissonProperties.getFile() != null) {
            try {
                InputStream is = getConfigStream();
                config = Config.fromYAML(is);
            } catch (IOException e) {
                // trying next format
                try {
                    InputStream is = getConfigStream();
                    config = Config.fromJSON(is);
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                    throw new IllegalArgumentException("Can't parse config", e1);
                }
            }
        } else if (redisProperties.getSentinel() != null || isSentinel) {
            String[] nodes = {};
            String sentinelMaster = null;

            if (redisProperties.getSentinel() != null) {
                Method nodesMethod = ReflectionUtils.findMethod(Sentinel.class, "getNodes");
                Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());
                if (nodesValue instanceof String) {
                    nodes = convert(prefix, Arrays.asList(((String)nodesValue).split(",")));
                } else {
                    nodes = convert(prefix, (List<String>)nodesValue);
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
                    nodes = convertNodes(prefix, (List<Object>) (Object) b.getSentinel().getNodes());
                    sentinelUsername = b.getSentinel().getUsername();
                    sentinelPassword = b.getSentinel().getPassword();
                }
            }

            config = new Config();
            SentinelServersConfig c = config.useSentinelServers()
                    .setMasterName(sentinelMaster)
                    .addSentinelAddress(nodes)
                    .setSentinelPassword(sentinelPassword)
                    .setSentinelUsername(sentinelUsername)
                    .setDatabase(database)
                    .setUsername(username)
                    .setPassword(password)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(c);
        } else if ((clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null)
                    || isCluster) {

            String[] nodes = {};
            if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
                Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
                Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
                List<String> nodesObject = (List<String>)ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

                nodes = convert(prefix, nodesObject);
            }

            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getCluster() != null) {
                    nodes = convertNodes(prefix, (List<Object>) (Object) b.getCluster().getNodes());
                }
            }

            config = new Config();
            ClusterServersConfig c = config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setUsername(username)
                    .setPassword(password)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(c);
        } else {
            config = new Config();

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
                    .setUsername(username)
                    .setPassword(password)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(c);
        }
        if (redissonAutoConfigurationCustomizers != null) {
            for (RedissonAutoConfigurationCustomizer customizer : redissonAutoConfigurationCustomizers) {
                customizer.customize(config);
            }
        }
        return Redisson.create(config);
    }

    private void initSSL(BaseConfig<?> config) {
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
        String prefix = REDIS_PROTOCOL_PREFIX;
        Method isSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
        Method getSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "getSsl");
        if (isSSLMethod != null) {
            if ((Boolean) ReflectionUtils.invokeMethod(isSSLMethod, redisProperties)) {
                prefix = REDISS_PROTOCOL_PREFIX;
            }
        } else if (getSSLMethod != null) {
            Object ss = ReflectionUtils.invokeMethod(getSSLMethod, redisProperties);
            if (ss != null) {
                Method isEnabledMethod = ReflectionUtils.findMethod(ss.getClass(), "isEnabled");
                Boolean enabled = (Boolean) ReflectionUtils.invokeMethod(isEnabledMethod, ss);
                if (enabled) {
                    prefix = REDISS_PROTOCOL_PREFIX;
                }
            }
        }
        return prefix;
    }

    private String[] convertNodes(String prefix, List<Object> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (Object node : nodesObject) {
            Field hostField = ReflectionUtils.findField(node.getClass(), "host");
            Field portField = ReflectionUtils.findField(node.getClass(), "port");
            ReflectionUtils.makeAccessible(hostField);
            ReflectionUtils.makeAccessible(portField);
            String host = (String) ReflectionUtils.getField(hostField, node);
            int port = (int) ReflectionUtils.getField(portField, node);
            nodes.add(prefix + host + ":" + port);
        }
        return nodes.toArray(new String[0]);
    }

    private String[] convert(String prefix, List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
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
