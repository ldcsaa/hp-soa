[[用户指南](user_guide.md)]

---

## 一、概述
&nbsp;&nbsp;HP-SOA 的 [hp-soa-starter-data-redis](../../hp-soa-starter/hp-soa-starter-data-redis) 模块提供了基于[Redisson](https://github.com/redisson/redisson)的Redis接入功能，支持同时访问多个Redis实例。

&nbsp;&nbsp;预定义 Redis Template（参考：[SoaDefaultRedisConfig](../../hp-soa-starter/hp-soa-starter-data-redis/src/main/java/io/github/hpsocket/soa/starter/data/redis/config/SoaDefaultRedisConfig.java)）：
- ***redisTemplate：*** 通用对象 Redis Template。
- ***stringRedisTemplate：*** 字符串 Redis Template。
- ***jsonRedisTemplate：*** JSON Redis Template。对象序列化为 JSON Object 或 JSON Array。
- ***genericJsonRedisTemplate：*** 通用 JSON Redis Template。序列化为JSON格式，同时保留对象类型信息，可被反序列化为原始对象。
- ***kryoRedisTemplate：*** Kryo Redis Template。支持空值。
- ***kryoNotNullRedisTemplate：*** Kryo NOT-NULL Redis Template。不支持空值。
- ***reactiveRedisTemplate：*** 响应式通用对象 Redis Template。
- ***reactiveStringRedisTemplate：*** 响应式字符串 Redis Template。

&nbsp;&nbsp;[hp-soa-starter-data-redis](../../hp-soa-starter/hp-soa-starter-data-redis) 提供了默认 Redis Cache Manager 用于支持 Spring Data 缓存注解，预定义以下 Cache Name（参考：[SoaDefaultRedisConfig](../../hp-soa-starter/hp-soa-starter-data-redis/src/main/java/io/github/hpsocket/soa/starter/data/redis/config/SoaDefaultRedisConfig.java)）：
- ***10s：*** 10秒
- ***30s：*** 30秒
- ***1m：*** 1分钟
- ***5m：*** 5分钟
- ***15m：*** 15分钟
- ***30m：*** 30分钟
- ***1h：*** 1小时
- ***3h：*** 3小时
- ***6h：*** 6小时
- ***12h：*** 12小时
- ***1d：*** 1天
- ***3d：*** 3天
- ***7d：*** 7天
- ***15d：*** 15天
- ***30d：*** 30天
- ***90d：*** 90天
- ***180d：*** 180天
- ***1y：*** 1年



## 二、接入步骤

#### 1. 引入 [hp-soa-starter-data-redis](../../hp-soa-starter/hp-soa-starter-data-redis) 依赖
- pom.xml 中添加 [hp-soa-starter-data-redis](../../hp-soa-starter/hp-soa-starter-data-redis) 依赖：
```xml
<dependencies>
    <!-- 引用 hp-soa-starter-data-redis-->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-data-redis</artifactId>
    </dependency>
</dependencies>
```

#### 2. 修改Redis配置

&nbsp;&nbsp;[hp-soa-starter-data-redis](../../hp-soa-starter/hp-soa-starter-data-redis) 内置支持连接3个Redis实例：

1. 默认Redis实例：
    - Redis配置前缀：*spring.data.redis*
    - Redisson配置前缀：*spring.redis.redisson*
2. 第二个Redis实例：
    - Redis配置前缀：*spring.data.redis-first*
    - Redisson配置前缀：*spring.redis.redisson-first*
3. 第三个Redis实例：
    - Redis配置前缀：*spring.data.redis-second*
    - Redisson配置前缀：*spring.redis.redisson-second*

&nbsp;&nbsp;配置示例参考[redis.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/redis.yml)，详细配置项目参考[Redisson配置文档](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter)。

#### 3. 代码开发

1. 方式一：通过 Redis Template 或 Redisson Client 访问 Redis

```java
@Component
public class MyService
{
    /* 注入 Redis Template Bean */
    @Autowired
    @Qualifier(SoaDefaultRedisConfig.redisTemplateBeanName)
    private RedisTemplate<String, ?> redisTemplate;
    
    /* 注入 Redisson Client Bean */
    @Autowired
    @Qualifier(SoaDefaultRedisConfig.redissonClientBeanName)
    private RedissonClient redissonClient;
}
```

2. 方式二：通过 Spring Data 缓存注解访问 Redis

```java
@Service
public class UserService
{​
    @Resource
    private UserMapper userMapper;
​
    /* 查询结果写入缓存，下次查询直接从缓存中取出 */
    @Cacheable(cacheNames = SoaDefaultRedisConfig.redisCacheManagerBeanName, cacheNames = "30m", key = "#id", unless = "#result != null")
    public User selectByPrimaryKey(String id)
    {
        return userMapper.selectByPrimaryKey(id);
    }
​
    /* 创建结果写入缓存 */
    @CachePut(value = SoaDefaultRedisConfig.redisCacheManagerBeanName, cacheNames = "30m", key = "#user.getId()")
    public User save(User user)
    {
        userMapper.saveUser(user);
        return user;
    }
​
    /* 更新结果写入缓存 */
    @CachePut(value = SoaDefaultRedisConfig.redisCacheManagerBeanName, cacheNames = "30m", key = "#user.getId()")
    public User update(User user)
    {
        userMapper.updateBySelective(user);
        return user;
    }

    /* 清除缓存 */
    @CacheEvict(value = SoaDefaultRedisConfig.redisCacheManagerBeanName, cacheNames = "30m", key = "#id")
    public void delete(String id)
    {
        userMapper.delete(id);
    }
}
}
```

---

[[用户指南](user_guide.md)]