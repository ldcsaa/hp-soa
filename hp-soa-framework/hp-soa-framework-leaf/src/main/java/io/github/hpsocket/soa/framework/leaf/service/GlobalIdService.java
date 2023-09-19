package io.github.hpsocket.soa.framework.leaf.service;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局 ID 服务
 */
public interface GlobalIdService
{
	/** 默认段号标签 */
	final String UNIQUE_SEGMENT_TAG	= "__default__";
	/** 测试段号标签 */
	final String TEST_SEGMENT_TAG	= "__test__";
	
	/** 段号 ID 生成器 Bean 名称 */
	final String LEAF_SEGMENT_ID_GENERATOR_BEAN		= "leafSegmentIdGenerator";
	/** 雪花 ID 生成器 Bean 名称 */
	final String LEAF_SNOWFLAKE_ID_GENERATOR_BEAN	= "leafSnowflakeIdGenerator";
	
	/** 通过 snowflake 方式获取 ID */
	long getSnowflakeId();
	/** 通过 segment 方式获取指定 tag 的 ID */
	long getSegmentId(String tag);
	/** 通过 segment 方式获取全局唯一 ID */
	long getUniqueSegmentId();
	/** 通过 segment 方式获取指定 tag 的 int 类型 ID */
	int getIntSegmentId(String tag);

	/** 通过 snowflake 方式批量获取 ID */
	long[] getBatchSnowflakeId(int batchSize);
	/** 通过 segment 方式批量获取指定 tag 的 ID */
	long[] getBatchSegmentId(String tag, int batchSize);
	/** 通过 segment 方式批量获取全局唯一 ID */
	long[] getBatchUniqueSegmentId(int batchSize);
	/** 通过 segment 方式批量获取指定 tag 的 int 类型 ID */
	int[] getBatchIntSegmentId(String tag, int batchSize);
	
	/** 解码 snowflake ID */
	SnowflakeId decodeSnowflakeId(long snowflakeId);
	/** 解码 snowflake ID */
	SnowflakeId decodeSnowflakeId(long snowflakeId, long twepoch);
	
	/** 获取雪花 ID 生成器 */
	IdGen getLeafSnowflakeIdGenerator();
	/** 获取段号 ID 生成器 */
	IdGen getLeafSegmentIdGenerator();
	
	/** 是否支持生成雪花 ID */
	boolean isSnowflakeIdSupported();
	/** 是否支持生成段号 ID */
	boolean isSegmentIdSupported();
	
	@Getter
	@AllArgsConstructor
	@SuppressWarnings("serial")
	public static class SnowflakeId implements Serializable
	{
		private long timestamp;
		private int workerId;
		private int sequence;
		
	    @Override
	    public String toString()
	    {
	        final StringBuilder sb = new StringBuilder();
	        
	        return sb.append("{timestamp=").append(timestamp)
                     .append(", workerId=").append(workerId)
                     .append(", sequence=").append(sequence)
                     .append('}')
                     .toString();
	    }
	}
}
