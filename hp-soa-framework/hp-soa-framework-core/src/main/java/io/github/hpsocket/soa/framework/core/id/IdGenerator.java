package io.github.hpsocket.soa.framework.core.id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator
{
	public static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	
	private static Sequence SEQUENCE = new Sequence();
	
	public static final void resetSequence(long workerId, long datacenterId)
	{
		SEQUENCE = new Sequence(workerId, datacenterId);
	}
	
	public static final long nextId()
	{
		return SEQUENCE.nextId();
	}
	
	public static final String nextIdStr()
	{
		return String.valueOf(nextId());
	}
	
	public static final String nextUUID()
	{
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return new UUID(random.nextLong(), random.nextLong()).toString();
	}
	
	public static final String nextCompactUUID()
	{
		return nextUUID().replaceAll("-", "");
	}
	
	public static final String curMillisecondStr()
	{
		return LocalDateTime.now().format(DATE_TIME_PATTERN);
	}
}
