
package io.github.hpsocket.soa.framework.leaf.snowflake;

import io.github.hpsocket.soa.framework.leaf.common.Result;
import io.github.hpsocket.soa.framework.leaf.common.Status;
import io.github.hpsocket.soa.framework.leaf.common.Utils;
import io.github.hpsocket.soa.framework.leaf.service.IdGen;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import com.google.common.base.Preconditions;

/** Snowflake ID 生成器 */
@Slf4j
public class SnowflakeIdGenImpl implements IdGen
{

    private final long twepoch;
    private final long timeStampBits = 41L;
    private final long workerIdBits = 10L;
    private final long maxWorkerId = ~(-1L << workerIdBits);// 最大能够分配的workerid
                                                            // =1023
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private final long maxTimeStamp;
    private static final Random RANDOM = new Random();

    public SnowflakeIdGenImpl(String zkAddress, int port, String leafName)
    {
        this(zkAddress, port, DEFAULT_SNOWFLAKE_TWEPOCK, leafName);
    }

    @Override
    public boolean init()
    {
        return true;
    }

    /**
     * @param zkAddress
     *            zk地址
     * @param port
     *            snowflake监听端口
     * @param twepoch
     *            起始的时间戳
     */
    public SnowflakeIdGenImpl(String zkAddress, int port, long twepoch, String leafName)
    {
        this.twepoch = twepoch;
        this.maxTimeStamp = ~(-1L << timeStampBits) + twepoch;
        Preconditions.checkArgument(timeGen() > twepoch, "Snowflake not support twepoch gt currentTime");
        
        final String ip = Utils.getIp();
        SnowflakeZookeeperHolder holder = new SnowflakeZookeeperHolder(ip, String.valueOf(port), zkAddress, leafName);
        log.info("twepoch:{} ,ip:{} ,zkAddress:{} port:{}", twepoch, ip, zkAddress, port);
        boolean initFlag = holder.init();
        
        if(initFlag)
        {
            workerId = holder.getWorkerID();
            log.info("START SUCCESS USE ZK WORKERID-{}", workerId);
        }
        else
        {
            Preconditions.checkArgument(initFlag, "Snowflake Id Gen is not init ok");
        }
        
        Preconditions.checkArgument(workerId >= 0 && workerId <= maxWorkerId, "workerID must gte 0 and lte 1023");
    }

    @Override
    public synchronized Result get(String key)
    {
        long timestamp = timeGen();
        
        if(timestamp < lastTimestamp)
        {
            long offset = lastTimestamp - timestamp;
            
            if(timestamp > maxTimeStamp)
            {
                return new Result(-4, Status.EXCEPTION);
            }
            
            if(offset <= 5)
            {
                try
                {
                    wait(offset << 1);
                    timestamp = timeGen();
                    
                    if(timestamp < lastTimestamp)
                    {
                        return new Result(-1, Status.EXCEPTION);
                    }
                }
                catch(InterruptedException e)
                {
                    log.error("wait interrupted");
                    return new Result(-2, Status.EXCEPTION);
                }
            }
            else
            {
                return new Result(-3, Status.EXCEPTION);
            }
        }
        
        if(lastTimestamp == timestamp)
        {
            sequence = (sequence + 1) & sequenceMask;
            
            if(sequence == 0)
            {
                // seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        else
        {
            // 如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        
        return new Result(id, Status.SUCCESS);

    }

    protected long tilNextMillis(long lastTimestamp)
    {
        long timestamp = timeGen();
        
        while(timestamp <= lastTimestamp)
        {
            timestamp = timeGen();
        }
        
        return timestamp;
    }

    protected long timeGen()
    {
        return System.currentTimeMillis();
    }

    public long getWorkerId()
    {
        return workerId;
    }
}
