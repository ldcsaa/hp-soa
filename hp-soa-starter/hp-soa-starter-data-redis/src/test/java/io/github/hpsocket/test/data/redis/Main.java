package io.github.hpsocket.test.data.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.redisson.client.DefaultNettyHook;
import org.redisson.client.NettyHook;
import org.redisson.client.codec.Codec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.ReplicatedServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.redisson.connection.ConnectionListener;
import org.redisson.connection.ConnectionManager;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.util.StopWatch;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import io.github.hpsocket.soa.starter.data.redis.serializer.KryoRedisSerializer;

import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;

public class Main
{
    private static final FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
    private static final GenericFastJsonRedisSerializer genericFastJsonRedisSerializer1 = new GenericFastJsonRedisSerializer(true);
    private static final GenericFastJsonRedisSerializer genericFastJsonRedisSerializer2 = new GenericFastJsonRedisSerializer(false);

    private static final KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<Object>();
    
    public static void main(String[] args)
    {
        final int COUNT = 100000;
        
        MyClass my = new MyClass();
        my.setName("Kingfisher");
        my.setAge(23);
        
        testFastJson(my, false);
        testFastJson(my, true);
        testGenericFastJson1(my);
        testGenericFastJson2(my);
        testKryo(my);
        
        StopWatch sw = new StopWatch("serial");
        
        sw.start();
        for(int i = 0; i < COUNT; i++)
        {
            testFastJson(my, false);
        }
        sw.stop();
        System.out.println("testFastJson - 1: " + sw.getLastTaskTimeMillis());
        
        sw.start();
        for(int i = 0; i < COUNT; i++)
        {
            testFastJson(my, true);
        }
        sw.stop();
        System.out.println("testFastJson - 2: " + sw.getLastTaskTimeMillis());
        
        sw.start();
        for(int i = 0; i < COUNT; i++)
        {
            testGenericFastJson1(my);
        }
        sw.stop();
        System.out.println("testGenericFastJson - 1: " + sw.getLastTaskTimeMillis());
        
        sw.start();
        for(int i = 0; i < COUNT; i++)
        {
            testGenericFastJson2(my);
        }
        sw.stop();
        System.out.println("testGenericFastJson - 2: " + sw.getLastTaskTimeMillis());
        
        sw.start();
        for(int i = 0; i < COUNT; i++)
        {
            testKryo(my);
        }
        sw.stop();
        System.out.println("testKryo: " + sw.getLastTaskTimeMillis());
        
    }

    @SuppressWarnings("unused")
    private static void testFastJson(MyClass my, boolean toJavaObj)
    {
        byte[] bytes = fastJsonRedisSerializer.serialize(my);
        Object obj = fastJsonRedisSerializer.deserialize(bytes);
        
        if(toJavaObj)
        {
            Object obj2 = ((JSONObject)obj).toJavaObject(MyClass.class);
        }
        
        //System.out.println(obj2);
        
    }

    @SuppressWarnings("unused")
    private static void testGenericFastJson1(MyClass my)
    {
        byte[] bytes = genericFastJsonRedisSerializer1.serialize(my);
        Object obj = genericFastJsonRedisSerializer1.deserialize(bytes);
        
        //System.out.println(obj);
    }

    @SuppressWarnings("unused")
    private static void testGenericFastJson2(MyClass my)
    {
        byte[] bytes = genericFastJsonRedisSerializer2.serialize(my);
        Object obj = genericFastJsonRedisSerializer2.deserialize(bytes);
        
        //System.out.println(obj);
    }

    @SuppressWarnings("unused")
    private static void testKryo(MyClass my)
    {
        byte[] bytes = kryoRedisSerializer.serialize(my);
        Object obj = kryoRedisSerializer.deserialize(bytes);
        
        //System.out.println(obj);
    }
}

@Getter
@Setter
class MyClass
{
    String name;
    Integer age;

    ///*
    private RedisProperties redisProperties = new RedisProperties();
    
    private SentinelServersConfig sentinelServersConfig = new SentinelServersConfig();

    private MasterSlaveServersConfig masterSlaveServersConfig = new MasterSlaveServersConfig();

    private SingleServerConfig singleServerConfig;

    private ClusterServersConfig clusterServersConfig = new ClusterServersConfig();
    //*/
    
    private ReplicatedServersConfig replicatedServersConfig = new ReplicatedServersConfig();

    private ConnectionManager connectionManager;

    private int threads = 16;

    private int nettyThreads = 32;

    private Codec codec;

    private ExecutorService executor;

    private boolean referenceEnabled = true;

    private TransportMode transportMode = TransportMode.NIO;

    private EventLoopGroup eventLoopGroup;

    private long lockWatchdogTimeout = 30 * 1000;

    private boolean checkLockSyncedSlaves = true;

    private long slavesSyncTimeout = 1000;

    private long reliableTopicWatchdogTimeout = TimeUnit.MINUTES.toMillis(10);

    private boolean keepPubSubOrder = true;

    private boolean useScriptCache = false;

    private int minCleanUpDelay = 5;

    private int maxCleanUpDelay = 30*60;

    private int cleanUpKeysAmount = 100;

    private NettyHook nettyHook = new DefaultNettyHook();

    private ConnectionListener connectionListener;

    private boolean useThreadClassLoader = true;

    
    @Override
    public String toString()
    {
        return String.format("{name: %s, age: %d}", name, age);
    }
}