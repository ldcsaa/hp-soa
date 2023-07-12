package com.lcb.hapi.demo.test;

import com.lcb.soa.misc.cache.Cache;
import com.lcb.soa.misc.cache.ICacheDao;
import com.lcb.soa.misc.common.util.GeneralHelper;
import com.lcb.soa.misc.common.util.Pair;
import com.lcb.soa.misc.server.spring.main.AppStarter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.lcb.soa.misc.common.helper.CommonHelper.printStep;
import static com.lcb.soa.misc.common.helper.CommonHelper.printlnf;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppStarter.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class TestCache {
    @Autowired
    private ICacheDao cacheDao;
    @Autowired
    private Cache cache;
    
    @Autowired
    @Qualifier("jedisCacheConnFactory")
    private JedisConnectionFactory jedisCacheConnFactory;

    @BeforeClass
    public static void beforeClass() throws Exception {

    }

    @AfterClass
    public static void afterClass() {
        GeneralHelper.waitFor(1, TimeUnit.SECONDS);
        printlnf("bye ~ bye ~");
    }

    @Test
    public void step_1() throws Exception {
        printStep(1, true);
        
        cacheDao.delete("dao_key");
        cacheDao.delete("dao_key1");
        ///*
        Map<Integer, Pair<Boolean, Double>> map = new HashMap<>();
        map.put(123, new Pair<>(Boolean.TRUE, 3.14));
        
        cacheDao.set("dao_key1", 1800, map);
        
        Map<?, ?> map2 = (Map<?, ?>)cacheDao.get("dao_key1");
        System.out.println(map2);
		//*/
        long val = cacheDao.incr("dao_key", 2, 0, 1800);
        val = cacheDao.incr("dao_key", 2, 0, 1800);
        val = (Integer)cacheDao.get("dao_key");
        val = cacheDao.decr("dao_key", 2, 0, 1800);
        val = cacheDao.decr("dao_key", 2, 0, 1800);
        val = cacheDao.decr("dao_key", 2, 0, 1800);
        val = (Integer)cacheDao.get("dao_key");
        val = (Integer)cacheDao.get("dao_key");
        val = cacheDao.decr("dao_key", 2, 0, 1800);
        val = cacheDao.decr("dao_key", 2, 0, 1800);
        val = cacheDao.decr("dao_key", 2, 0, 1800);
        val = cacheDao.decr("dao_key", 2, 0, 1800);

        System.out.println(val);
        printStep(1, false);
    }

    @Test
    public void step_2() throws Exception {
        printStep(2, true);
        
        cache.asynRemove("cache_key");

        long val = cache.incrOne("cache_key");
        val = cache.incrOne("cache_key");
        val = cache.incrOne("cache_key");
        val = (Integer)cache.get("cache_key");
        val = (Integer)cache.get("cache_key");
        val = cache.decrOne("cache_key");
        val = cache.decrOne("cache_key");
        val = cache.decrOne("cache_key");
        val = cache.decrOne("cache_key");
        val = (Integer)cache.get("cache_key");
        val = (Integer)cache.get("cache_key");
        val = cache.decrOne("cache_key");
        val = cache.decrOne("cache_key");
        
        System.out.println(val);
        printStep(2, false);
    }
    
    @Test
    public void step_3()
    {
    	cache.set("abc:1", 100, 10000000L);
    	cache.set("abc:2", 200, 10000000L);
    	cache.set("abc:3", 300, 10000000L);
    	cache.set("abc:4", 400, 10000000L);
    	cache.set("abc:5", 500, 10000000L);
    	cache.set("abc:6", 600, 10000000L);
    	cache.set("abc:7", 700, 10000000L);
    	cache.set("abc:8", 800, 10000000L);
    	cache.set("abc:9", 900, 10000000L);
    	cache.set("abc:10", 1000, 10000000L);
    	cache.set("abc:11", 1100, 10000000L);
    	cache.set("abc:12", 1200, 10000000L);
    	cache.set("abc:13", 1200, 10000000L);
    	cache.set("abc:14", 1200, 10000000L);
    	cache.set("abc:15", 1200, 10000000L);
    	cache.set("abc:16", 1200, 10000000L);
    	cache.set("abc:17", 1200, 10000000L);
    	cache.set("abc:18", 1200, 10000000L);
    	cache.set("abc:19", 1200, 10000000L);
    	cache.set("abc:20", 1200, 10000000L);
    	cache.set("abc:21", 1200, 10000000L);
    	cache.set("abc:22", 1200, 10000000L);
    	cache.set("abc:23", 1200, 10000000L);
    	cache.set("abc:24", 1200, 10000000L);
    	
    	long dels = cache.deleteKeys("abc:*", 1000);
    	System.out.println(dels);
    	dels = cache.deleteKeys("abc:*", 1000);
    	System.out.println(dels);
    }

}
