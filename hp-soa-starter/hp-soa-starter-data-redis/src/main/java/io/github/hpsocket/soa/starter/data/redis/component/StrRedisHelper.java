package io.github.hpsocket.soa.starter.data.redis.component;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import lombok.Getter;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <b>StringRedisTemplate 工具类</b><br>
 * 封装了常用的 Redis 操作，包括字符串、哈希、列表、集合、有序集合等数据结构的操作。
 */
@Getter
public class StrRedisHelper
{
    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;
    private final HashOperations<String, String, String> hashOps;
    private final ListOperations<String, String> listOps;
    private final SetOperations<String, String> setOps;
    private final ZSetOperations<String, String> zSetOps;

    /**
     * 构造函数，初始化 Redis 操作模板和相关操作接口
     *
     * @param stringRedisTemplate 字符串专用的 Redis 操作模板
     *
     */
    public StrRedisHelper(StringRedisTemplate stringRedisTemplate)
    {
        this.redisTemplate = stringRedisTemplate;
        valueOps = stringRedisTemplate.opsForValue();
        hashOps = stringRedisTemplate.opsForHash();
        listOps = stringRedisTemplate.opsForList();
        setOps = stringRedisTemplate.opsForSet();
        zSetOps = stringRedisTemplate.opsForZSet();
    }

    /**
     * 设置缓存
     *
     * @param key    缓存键
     * @param value  缓存值
     * @param expire 缓存时间
     */
    public void set(String key, String value, Duration expire)
    {
        if(expire == null)
            set(key, value);
        else
            setEx(key, value, expire);
    }

    /**
     * 存放 key-value 对到 Redis
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value)
    {
        valueOps.set(key, value);
    }

    /**
     * 存放 key value 对到 redis，用于自定义序列化的方式
     *
     * @param key    redis key
     * @param mapper 序列化转换
     */
    public void set(String key, String value, Function<String, byte[]> mapper)
    {
        redisTemplate.execute((RedisCallback<Object>)redis -> redis.stringCommands().set(keySerialize(key), mapper.apply(value)));
    }

    /**
     * 当 key 不存在时设置 key-value（分布式锁/幂等控制）
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     * @return true-设置成功（获取锁成功），false-设置失败（key已存在）
     */
    public boolean setIfAbsent(String key, String value, Duration expire)
    {
        Boolean result = valueOps.setIfAbsent(key, value, expire);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 存放 key-value 对到 Redis，并设置过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public void setEx(String key, String value, Duration timeout)
    {
        valueOps.set(key, value, timeout);
    }

    /**
     * 存放 key-value 对到 Redis，并设置过期时间（以秒为单位）
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void setEx(String key, String value, Long seconds)
    {
        valueOps.set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 存放 key-value 对到 Redis，并设置过期时间（自定义单位）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void setEx(String key, String value, long timeout, TimeUnit unit)
    {
        valueOps.set(key, value, timeout, unit);
    }

    /**
     * 获取 key 对应的值
     *
     * @param key 键
     * @return 值
     */
    @Nullable
    public String get(String key)
    {
        return valueOps.get(key);
    }

    /**
     * 返回 key 所关联的 value 值
     *
     * @param key    redis key
     * @param mapper 函数式
     * @return String
     */
    @Nullable
    public String get(String key, Function<byte[], String> mapper)
    {
        return redisTemplate.execute((RedisCallback<String>)redis ->
        {
            byte[] value = redis.stringCommands().get(keySerialize(key));
            if(value == null)
                return null;

            return mapper.apply(value);
        });
    }

    /**
     * 获取缓存值，如果缓存不存在则通过加载器获取值并设置缓存
     *
     * @param key    键
     * @param loader 加载器
     * @return 值
     */
    @Nullable
    public String get(String key, Supplier<String> loader)
    {
        String value = this.get(key);
        if(value != null)
            return value;

        value = loader.get();
        if(value == null)
            return null;

        this.set(key, value);
        return value;
    }

    /**
     * 获取 key 对应的值，并删除 key
     *
     * @param key 键
     * @return 值
     */
    @Nullable
    public String getAndDel(String key)
    {
        String o = this.get(key);
        this.del(key);
        return o;
    }

    /**
     * 删除指定的 key
     *
     * @param key 键
     * @return 是否删除成功
     */
    public Boolean del(String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个 key
     *
     * @param keys 键数组
     * @return 删除的 key 数量
     */
    public Long del(String... keys)
    {
        return del(Arrays.asList(keys));
    }

    /**
     * 删除多个 key
     *
     * @param keys 键集合
     * @return 删除的 key 数量
     */
    public Long del(Collection<String> keys)
    {
        return redisTemplate.delete(keys);
    }

    /**
     * 查找所有符合给定模式的 key
     *
     * @param pattern 匹配模式
     * @return 符合条件的 key 集合
     */
    public Set<String> keys(String pattern)
    {
        return redisTemplate.keys(pattern);
    }


    /**
     * redis scan，count 默认 100
     *
     * @param pattern 匹配表达式
     * @return 扫描结果
     */
    public Set<String> scan(String pattern)
    {
        return scan(pattern, 100L);
    }

    /**
     * redis scan
     *
     * @param pattern 匹配表达式
     * @param count   一次扫描的数量, redis 默认为 10
     * @return 扫描结果
     */
    public Set<String> scan(String pattern, @Nullable Long count)
    {
        final Set<String> keySet = new HashSet<>();
        scan(pattern, count, keySet::add);
        return keySet;
    }

    /**
     * redis scan, count 默认 100
     *
     * @param pattern  匹配表达式
     * @param consumer 消费者
     */
    public void scan(String pattern, Consumer<String> consumer)
    {
        scan(pattern, 100L, consumer);
    }

    /**
     * redis scan
     *
     * @param pattern  匹配表达式
     * @param count    一次扫描的数量
     * @param consumer 消费者
     */
    public void scan(String pattern, @Nullable Long count, Consumer<String> consumer)
    {
        scanBytes(pattern, count, (bytes) -> consumer.accept(keyDeserialize(bytes)));
    }

    /**
     * redis scan
     *
     * @param pattern  匹配表达式
     * @param count    一次扫描的数量
     * @param consumer 消费者
     */
    public void scanBytes(String pattern, @Nullable Long count, Consumer<byte[]> consumer)
    {
        redisTemplate.execute((RedisCallback<String>)redis ->
        {
            ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions().match(pattern);
            if(count != null)
                builder.count(count);

            RedisKeyCommands commands = redis.keyCommands();
            try(Cursor<byte[]> cursor = commands.scan(builder.build()))
            {
                cursor.forEachRemaining(consumer);
            }
            return null;
        });
    }

    /**
     * 扫描集合中的成员
     *
     * @param key     键
     * @param pattern 匹配表达式
     * @param count   一次扫描的数量
     * @return 扫描结果
     */
    Cursor<?> scan(String key, String pattern, long count)
    {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
        return scan(key, options);
    }

    /**
     * 扫描集合中的成员
     *
     * @param key     键
     * @param options 扫描选项
     * @return 扫描结果
     */
    Cursor<?> scan(String key, ScanOptions options)
    {
        return setOps.scan(key, options);
    }

    /**
     * 同时设置多个 key-value 对
     *
     * @param map key-value Map
     */
    public void mSet(Map<String, String> map)
    {
        valueOps.multiSet(map);
    }

    /**
     * 获取多个 key 对应的值
     *
     * @param keys 键数组
     * @return 值列表
     */
    public List<String> mGet(String... keys)
    {
        return mGet(Arrays.asList(keys));
    }

    /**
     * 获取多个 key 对应的值
     *
     * @param keys 键集合
     * @return 值列表
     */
    public List<String> mGet(Collection<String> keys)
    {
        return valueOps.multiGet(keys);
    }

    /**
     * 将 key 中存储的数字值减一
     *
     * @param key 键
     * @return 减一后的值
     */
    public Long decr(String key)
    {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 将 key 中存储的数字值减去指定的值
     *
     * @param key       键
     * @param longValue 减去的值
     * @return 减去后的值
     */
    public Long decrBy(String key, long longValue)
    {
        return redisTemplate.opsForValue().decrement(key, longValue);
    }

    /**
     * 将 key 中存储的数字值减去指定的值
     *
     * @param key         键
     * @param doubleValue 减去的值
     * @return 减去后的值
     */
    public Double decrByFloat(String key, double doubleValue)
    {
        return valueOps.increment(key, -doubleValue);
    }

    /**
     * 将 key 中存储的数字值加一
     *
     * @param key 键
     * @return 加一后的值
     */
    public Long incr(String key)
    {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 将 key 中存储的数字值加上指定的值
     *
     * @param key         键
     * @param doubleValue 加上的值
     * @return 加上后的值
     */
    public Double incrByFloat(String key, double doubleValue)
    {
        return valueOps.increment(key, doubleValue);
    }

    /**
     * 将 key 中存储的数字值加上指定的值
     *
     * @param key       键
     * @param longValue 加上的值
     * @return 加上后的值
     */
    public Long incrBy(String key, long longValue)
    {
        return redisTemplate.opsForValue().increment(key, longValue);
    }

    /**
     * 获取 key 对应的递增/递减值
     *
     * @param key 键
     * @return 递增/递减值
     */
    public Long getCrCounter(String key)
    {
        return GeneralHelper.str2Long(valueOps.get(key));
    }

    /**
     * 获取 key 对应的递增/递减值
     *
     * @param key 键
     * @return 递增/递减值
     */
    public Double getCrCounterFloat(String key)
    {
        return GeneralHelper.str2Double(valueOps.get(key));
    }

    /**
     * 检查 key 是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean exists(String key)
    {
        return redisTemplate.hasKey(key);
    }

    /**
     * 随机返回一个 key
     *
     * @return 随机 key
     */
    public String randomKey()
    {
        return redisTemplate.randomKey();
    }

    /**
     * 将 key 改名为 newkey
     *
     * @param oldkey 旧键
     * @param newkey 新键
     */
    public void rename(String oldkey, String newkey)
    {
        redisTemplate.rename(oldkey, newkey);
    }

    /**
     * 将 key 移动到指定的数据库
     *
     * @param key     键
     * @param dbIndex 数据库索引
     * @return 是否移动成功
     */
    public Boolean move(String key, int dbIndex)
    {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 为 key 设置过期时间（以秒为单位）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @return 是否设置成功
     */
    public Boolean expire(String key, long seconds)
    {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 为 key 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @return 是否设置成功
     */
    public Boolean expire(String key, Duration timeout)
    {
        return expire(key, timeout.getSeconds());
    }

    /**
     * 为 key 设置过期时间（指定时间点）
     *
     * @param key  键
     * @param date 过期时间点
     * @return 是否设置成功
     */
    public Boolean expireAt(String key, Date date)
    {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 为 key 设置过期时间（指定时间戳）
     *
     * @param key      键
     * @param unixTime 过期时间戳
     * @return 是否设置成功
     */
    public Boolean expireAt(String key, long unixTime)
    {
        return expireAt(key, new Date(unixTime));
    }

    /**
     * 为 key 设置过期时间（以毫秒为单位）
     *
     * @param key          键
     * @param milliseconds 过期时间（毫秒）
     * @return 是否设置成功
     */
    public Boolean pexpire(String key, long milliseconds)
    {
        return redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置 key 的值并返回旧值
     *
     * @param key   键
     * @param value 新值
     * @return 旧值
     */
    public String getSet(String key, String value)
    {
        return valueOps.getAndSet(key, value);
    }

    /**
     * 移除 key 的过期时间，使其永久有效
     *
     * @param key 键
     * @return 是否移除成功
     */
    public Boolean persist(String key)
    {
        return redisTemplate.persist(key);
    }

    /**
     * 返回 key 所存储的值的类型
     *
     * @param key 键
     * @return 值类型
     */
    public String type(String key)
    {
        return redisTemplate.type(key).code();
    }

    /**
     * 返回 key 的剩余生存时间（以秒为单位）
     *
     * @param key 键
     * @return 剩余生存时间（秒）
     */
    public Long ttl(String key)
    {
        return redisTemplate.getExpire(key);
    }

    /**
     * 返回 key 的剩余生存时间（以毫秒为单位）
     *
     * @param key 键
     * @return 剩余生存时间（毫秒）
     */
    public Long pttl(String key)
    {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置哈希表中指定字段的值
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     */
    public void hSet(String key, String field, String value)
    {
        hashOps.put(key, field, value);
    }

    /**
     * 同时设置哈希表中多个字段的值
     *
     * @param key  键
     * @param hash 字段-值映射
     */
    public void hmSet(String key, Map<String, String> hash)
    {
        hashOps.putAll(key, hash);
    }

    /**
     * 获取哈希表中指定字段的值
     *
     * @param key   键
     * @param field 字段
     * @return 值
     */
    public String hGet(String key, String field)
    {
        return hashOps.get(key, field);
    }

    /**
     * 获取哈希表中多个字段的值
     *
     * @param key    键
     * @param fields 字段数组
     * @return 值列表
     */
    public List hmGet(String key, String... fields)
    {
        return hmGet(key, Arrays.asList(fields));
    }

    /**
     * 获取哈希表中多个字段的值
     *
     * @param key      键
     * @param hashKeys 字段集合
     * @return 值列表
     */
    public List hmGet(String key, Collection<String> hashKeys)
    {
        return hashOps.multiGet(key, hashKeys);
    }

    /**
     * 删除哈希表中一个或多个字段
     *
     * @param key    键
     * @param fields 字段数组
     * @return 删除的字段数量
     */
    public Long hDel(String key, String... fields)
    {
        return hashOps.delete(key, (Object[])fields);
    }

    /**
     * 检查哈希表中是否存在指定字段
     *
     * @param key   键
     * @param field 字段
     * @return 是否存在
     */
    public Boolean hExists(String key, String field)
    {
        return hashOps.hasKey(key, field);
    }

    /**
     * 获取哈希表中所有字段和值
     *
     * @param key 键
     * @return 字段-值映射
     */
    public Map hGetAll(String key)
    {
        return hashOps.entries(key);
    }

    /**
     * 获取哈希表中所有字段的值
     *
     * @param key 键
     * @return 值列表
     */
    public List hVals(String key)
    {
        return hashOps.values(key);
    }

    /**
     * 获取哈希表中所有字段
     *
     * @param key 键
     * @return 字段集合
     */
    public Set<String> hKeys(String key)
    {
        return hashOps.keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key 键
     * @return 字段数量
     */
    public Long hLen(String key)
    {
        return hashOps.size(key);
    }

    /**
     * 将 key 中存储的数字值减一
     *
     * @param key   键
     * @param field 字段
     * @return 减一后的值
     */
    public Long hDecr(String key, String field)
    {
        return hashOps.increment(key, field, -1);
    }

    /**
     * 将 key 中存储的数字值减去指定的值
     *
     * @param key       键
     * @param field     字段
     * @param longValue 减去的值
     * @return 减去后的值
     */
    public Long hDecrBy(String key, String field, long longValue)
    {
        return hashOps.increment(key, field, -longValue);
    }

    /**
     * 将 key 中存储的数字值减去指定的值
     *
     * @param key         键
     * @param doubleValue 减去的值
     * @return 减去后的值
     */
    public Double hDecrByFloat(String key, String field, double doubleValue)
    {
        return hashOps.increment(key, field, -doubleValue);
    }

    /**
     * 将 key 中存储的数字值加一
     *
     * @param key   键
     * @param field 字段
     * @return 加一后的值
     */
    public Long hIncr(String key, String field)
    {
        return hashOps.increment(key, field, 1);
    }

    /**
     * 将 key 中存储的数字值加上指定的值
     *
     * @param key       键
     * @param field     字段
     * @param longValue 加上的值
     * @return 加上后的值
     */
    public Long hIncrBy(String key, String field, long longValue)
    {
        return hashOps.increment(key, field, longValue);
    }

    /**
     * 将 key 中存储的数字值加上指定的值
     *
     * @param key         键
     * @param field       字段
     * @param doubleValue 加上的值
     * @return 加上后的值
     */
    public Double hIncrByFloat(String key, String field, double doubleValue)
    {
        return hashOps.increment(key, field, doubleValue);
    }

    /**
     * 获取 key 对应的递增/递减值
     *
     * @param key   键
     * @param field 字段
     * @return 递增/递减值
     */
    public Long hGetCrCounter(String key, String field)
    {
        return GeneralHelper.str2Long(hashOps.get(key, field));
    }

    /**
     * 获取 key 对应的递增/递减值
     *
     * @param key   键
     * @param field 字段
     * @return 递增/递减值
     */
    public Double getCrCounterFloat(String key, Object field)
    {
        return GeneralHelper.str2Double(hashOps.get(key, field));
    }

    /**
     * 扫描HASH中的成员
     *
     * @param key     键
     * @param pattern 匹配模式
     * @param count   扫描数量
     * @return 扫描结果
     */
    public Cursor<Map.Entry<String, String>> hScan(String key, String pattern, long count)
    {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
        return hScan(key, options);
    }

    /**
     * 扫描HASH中的成员
     *
     * @param key     键
     * @param options 扫描选项
     * @return 扫描结果
     */
    public Cursor<Map.Entry<String, String>> hScan(String key, ScanOptions options)
    {
        return hashOps.scan(key, options);
    }

    /**
     * 获取列表中指定下标的元素
     *
     * @param key   键
     * @param index 下标
     * @return 元素
     */
    public String lIndex(String key, long index)
    {
        return listOps.index(key, index);
    }

    /**
     * 获取列表的长度
     *
     * @param key 键
     * @return 列表长度
     */
    public Long lLen(String key)
    {
        return listOps.size(key);
    }

    /**
     * 移除并返回列表的头元素
     *
     * @param key 键
     * @return 头元素
     */
    public String lPop(String key)
    {
        return listOps.leftPop(key);
    }

    /**
     * 将一个或多个值插入到列表的头部
     *
     * @param key   键
     * @param value 值
     * @return 插入后列表的长度
     */
    public Long lPush(String key, String value)
    {
        return listOps.leftPush(key, value);
    }

    /**
     * 将一个或多个值插入到列表的头部
     *
     * @param key    键
     * @param values 值数组
     * @return 插入后列表的长度
     */
    public Long lPushAll(String key, String... values)
    {
        return listOps.leftPushAll(key, values);
    }

    /**
     * 设置列表中指定下标的元素
     *
     * @param key   键
     * @param index 下标
     * @param value 值
     */
    public void lSet(String key, long index, String value)
    {
        listOps.set(key, index, value);
    }

    /**
     * 移除列表中与指定值相等的元素
     *
     * @param key   键
     * @param count 移除数量
     * @param value 值
     * @return 移除的元素数量
     */
    public Long lRem(String key, long count, String value)
    {
        return listOps.remove(key, count, value);
    }

    /**
     * 获取列表中指定区间的元素
     *
     * @param key   键
     * @param start 起始下标
     * @param end   结束下标
     * @return 元素列表
     */
    public List lRange(String key, long start, long end)
    {
        return listOps.range(key, start, end);
    }

    /**
     * 对列表进行修剪，只保留指定区间的元素
     *
     * @param key   键
     * @param start 起始下标
     * @param end   结束下标
     */
    public void lTrim(String key, long start, long end)
    {
        listOps.trim(key, start, end);
    }

    /**
     * 移除并返回列表的尾元素
     *
     * @param key 键
     * @return 尾元素
     */
    public String rPop(String key)
    {
        return listOps.rightPop(key);
    }

    /**
     * 将一个或多个值插入到列表的尾部
     *
     * @param key   键
     * @param value 值
     * @return 插入后列表的长度
     */
    public Long rPush(String key, String value)
    {
        return listOps.rightPush(key, value);
    }

    /**
     * 将一个或多个值插入到列表的尾部
     *
     * @param key    键
     * @param values 值数组
     * @return 插入后列表的长度
     */
    public Long rPushAll(String key, String... values)
    {
        return listOps.rightPushAll(key, values);
    }

    /**
     * 将列表的尾元素弹出并插入到另一个列表的头部
     *
     * @param srcKey 源列表键
     * @param dstKey 目标列表键
     * @return 弹出的元素
     */
    public String rPopLPush(String srcKey, String dstKey)
    {
        return listOps.rightPopAndLeftPush(srcKey, dstKey);
    }

    /**
     * 将一个或多个成员添加到集合中
     *
     * @param key     键
     * @param members 成员数组
     * @return 添加的成员数量
     */
    public Long sAdd(String key, String... members)
    {
        return setOps.add(key, members);
    }

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key 键
     * @return 随机元素
     */
    public String sPop(String key)
    {
        return setOps.pop(key);
    }

    /**
     * 获取集合中的所有成员
     *
     * @param key 键
     * @return 成员集合
     */
    public Set sMembers(String key)
    {
        return setOps.members(key);
    }

    /**
     * 检查成员是否在集合中
     *
     * @param key    键
     * @param member 成员
     * @return 是否在集合中
     */
    public boolean sIsMember(String key, String member)
    {
        return setOps.isMember(key, member);
    }

    /**
     * 返回多个集合的交集
     *
     * @param key      键
     * @param otherKey 另一个集合键
     * @return 交集集合
     */
    public Set sInter(String key, String otherKey)
    {
        return setOps.intersect(key, otherKey);
    }

    /**
     * 返回多个集合的交集
     *
     * @param key       键
     * @param otherKeys 其他集合键集合
     * @return 交集集合
     */
    public Set sInter(String key, Collection<String> otherKeys)
    {
        return setOps.intersect(key, otherKeys);
    }

    /**
     * 返回集合中的一个随机成员
     *
     * @param key 键
     * @return 随机成员
     */
    public String sRandMember(String key)
    {
        return setOps.randomMember(key);
    }

    /**
     * 返回集合中的多个随机成员
     *
     * @param key   键
     * @param count 随机成员数量
     * @return 随机成员列表
     */
    public List sRandMember(String key, int count)
    {
        return setOps.randomMembers(key, count);
    }

    /**
     * 移除集合中的一个或多个成员
     *
     * @param key     键
     * @param members 成员数组
     * @return 移除的成员数量
     */
    public Long sRem(String key, String... members)
    {
        return setOps.remove(key, (Object[])members);
    }

    /**
     * 返回多个集合的并集
     *
     * @param key      键
     * @param otherKey 另一个集合键
     * @return 并集集合
     */
    public Set sUnion(String key, String otherKey)
    {
        return setOps.union(key, otherKey);
    }

    /**
     * 返回多个集合的并集
     *
     * @param key       键
     * @param otherKeys 其他集合键集合
     * @return 并集集合
     */
    public Set sUnion(String key, Collection<String> otherKeys)
    {
        return setOps.union(key, otherKeys);
    }

    /**
     * 返回多个集合的差集
     *
     * @param key      键
     * @param otherKey 另一个集合键
     * @return 差集集合
     */
    public Set sDiff(String key, String otherKey)
    {
        return setOps.difference(key, otherKey);
    }

    /**
     * 返回多个集合的差集
     *
     * @param key       键
     * @param otherKeys 其他集合键集合
     * @return 差集集合
     */
    public Set sDiff(String key, Collection<String> otherKeys)
    {
        return setOps.difference(key, otherKeys);
    }

    /**
     * 将一个或多个成员及其分数添加到有序集合中
     *
     * @param key    键
     * @param member 成员
     * @param score  分数
     * @return 是否添加成功
     */
    public Boolean zAdd(String key, String member, double score)
    {
        return zSetOps.add(key, member, score);
    }

    /**
     * 将一个或多个成员及其分数添加到有序集合中
     *
     * @param key          键
     * @param scoreMembers 成员-分数映射
     * @return 添加的成员数量
     */
    public Long zAdd(String key, Map<String, Double> scoreMembers)
    {
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        scoreMembers.forEach((k, v) ->
        {
            tuples.add(new DefaultTypedTuple<>(k, v));
        });

        return zSetOps.add(key, tuples);
    }

    /**
     * 获取有序集合的基数
     *
     * @param key 键
     * @return 有序集合的基数
     */
    public Long zCard(String key)
    {
        return zSetOps.zCard(key);
    }

    /**
     * 返回有序集合中分数在指定范围内的成员数量
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 成员数量
     */
    public Long zCount(String key, double min, double max)
    {
        return zSetOps.count(key, min, max);
    }

    /**
     * 为有序集合中的成员增加分数
     *
     * @param key    键
     * @param member 成员
     * @param score  增加的分数
     * @return 增加后的分数
     */
    public Double zDecrBy(String key, String member, double score)
    {
        return zSetOps.incrementScore(key, member, -score);
    }

    /**
     * 为有序集合中的成员增加分数
     *
     * @param key    键
     * @param member 成员
     * @param score  增加的分数
     * @return 增加后的分数
     */
    public Double zIncrBy(String key, String member, double score)
    {
        return zSetOps.incrementScore(key, member, score);
    }

    /**
     * 返回有序集合中指定区间的成员（按分数递增排序）
     *
     * @param key   键
     * @param start 起始下标
     * @param end   结束下标
     * @return 成员集合
     */
    public Set zRange(String key, long start, long end)
    {
        return zSetOps.range(key, start, end);
    }

    /**
     * 返回有序集合中指定区间的成员（按分数递减排序）
     *
     * @param key   键
     * @param start 起始下标
     * @param end   结束下标
     * @return 成员集合
     */
    public Set zRevRange(String key, long start, long end)
    {
        return zSetOps.reverseRange(key, start, end);
    }

    /**
     * 返回有序集合中分数在指定范围内的成员（按分数递增排序）
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 成员集合
     */
    public Set zRangeByScore(String key, double min, double max)
    {
        return zSetOps.rangeByScore(key, min, max);
    }


    /**
     * 返回有序集合中分数在指定范围内的成员（按分数递减排序）
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 成员集合
     */
    public Set zRevRangeByScore(String key, double min, double max)
    {
        return zSetOps.reverseRangeByScore(key, min, max);
    }

    /**
     * 返回有序集合中成员的排名（按分数递增排序）
     *
     * @param key    键
     * @param member 成员
     * @return 排名
     */
    public Long zRank(String key, String member)
    {
        return zSetOps.rank(key, member);
    }

    /**
     * 返回有序集合中成员的排名（按分数递减排序）
     *
     * @param key    键
     * @param member 成员
     * @return 排名
     */
    public Long zRevRank(String key, String member)
    {
        return zSetOps.reverseRank(key, member);
    }

    /**
     * 移除有序集合中的一个或多个成员
     *
     * @param key     键
     * @param members 成员数组
     * @return 移除的成员数量
     */
    public Long zRem(String key, String... members)
    {
        return zSetOps.remove(key, (Object[])members);
    }


    /**
     * 移除有序集合中的一个或多个成员
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 移除的成员数量
     */
    public Long zRemByScore(String key, double min, double max)
    {
        return zSetOps.removeRangeByScore(key, min, max);
    }

    /**
     * 返回有序集合中成员的分数
     *
     * @param key    键
     * @param member 成员
     * @return 分数
     */
    public Double zScore(String key, String member)
    {
        return zSetOps.score(key, member);
    }

    /**
     * 扫描有序集合中的成员
     *
     * @param key     键
     * @param pattern 匹配模式
     * @param count   扫描数量
     * @return 扫描结果
     */
    public Cursor<ZSetOperations.TypedTuple<String>> zScan(String key, String pattern, long count)
    {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
        return zScan(key, options);
    }

    /**
     * 扫描有序集合中的成员
     *
     * @param key     键
     * @param options 扫描选项
     * @return 扫描结果
     */
    public Cursor<ZSetOperations.TypedTuple<String>> zScan(String key, ScanOptions options)
    {
        return zSetOps.scan(key, options);
    }

    /**
     * 发布消息到指定频道
     *
     * @param channel 频道
     * @param message 消息
     * @param mapper  消息序列化函数
     * @return 接收到消息的客户端数量
     */
    @Nullable
    public Long publish(String channel, String message, Function<String, byte[]> mapper)
    {
        return redisTemplate.execute((RedisCallback<Long>)redis ->
        {
            byte[] channelBytes = keySerialize(channel);
            return redis.publish(channelBytes, mapper.apply(message));
        });
    }

    /**
     * 序列化 Redis 键
     *
     * @param redisKey Redis 键
     * @return 序列化后的字节数组
     */
    public byte[] keySerialize(@NonNull String redisKey)
    {
        RedisSerializer<String> keySerializer = (RedisSerializer<String>)this.redisTemplate.getKeySerializer();
        return keySerializer.serialize(redisKey);
    }

    /**
     * redisKey 序列化
     *
     * @param redisKey redisKey
     * @return byte array
     */
    public byte[] hashKeySerializer(@NonNull Object redisKey)
    {
        RedisSerializer<Object> hashKeySerializer = (RedisSerializer<Object>)this.redisTemplate.getHashKeySerializer();
        return hashKeySerializer.serialize(redisKey);
    }

    /**
     * redisKey 反序列化
     *
     * @param redisKey redisKey
     * @return String
     */
    public String keyDeserialize(@NonNull byte[] redisKey)
    {
        RedisSerializer<String> keySerializer = (RedisSerializer<String>)this.redisTemplate.getKeySerializer();
        return keySerializer.deserialize(redisKey);
    }

    /**
     * redisKey 反序列化
     *
     * @param redisKey redisKey
     * @return String
     */
    public String hashKeyDeserialize(@NonNull byte[] redisKey)
    {
        RedisSerializer<String> hashKeySerializer = (RedisSerializer<String>)this.redisTemplate.getHashKeySerializer();
        return hashKeySerializer.deserialize(redisKey);
    }

}
