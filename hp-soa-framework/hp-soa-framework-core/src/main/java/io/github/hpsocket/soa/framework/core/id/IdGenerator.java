package io.github.hpsocket.soa.framework.core.id;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** <b>ID 生成器</b> */
public class IdGenerator
{
    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    
    private static Sequence SEQUENCE = new Sequence();
    
    /**
     * 重置雪花 ID 生成器的 workerId 和 datacenterId <br>
     * @param workerId ：参考 {@linkplain Sequence#workerId}
     * @param datacenterId ：参考 {@linkplain Sequence#datacenterId}
     */
    public static final void resetSequence(long workerId, long datacenterId)
    {
        SEQUENCE = new Sequence(workerId, datacenterId);
    }
    
    /** 获取新雪花 ID */
    public static final long nextId()
    {
        return SEQUENCE.nextId();
    }
    
    /** 获取新雪花 ID */
    public static final String nextIdStr()
    {
        return String.valueOf(nextId());
    }
    
    /** 获取新雪花 UUID */
    public static final String nextUUID()
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }
    
    /** 获取新雪花 UUID，并移除 UUID 中的 '-' */
    public static final String nextCompactUUID()
    {
        return nextUUID().replaceAll("-", "");
    }
    
    /** 获取当前时间并格式化为字符串（精确到毫秒） */
    public static final String curMillisecondStr()
    {
        return ZonedDateTime.now().format(DATE_TIME_PATTERN);
    }
}
