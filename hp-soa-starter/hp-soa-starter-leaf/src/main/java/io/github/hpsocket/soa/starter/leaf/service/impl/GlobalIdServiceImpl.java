package io.github.hpsocket.soa.starter.leaf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.leaf.common.BatchResult;
import io.github.hpsocket.soa.framework.leaf.common.Result;
import io.github.hpsocket.soa.framework.leaf.common.Status;
import io.github.hpsocket.soa.framework.leaf.service.IdGen;
import lombok.Getter;
import io.github.hpsocket.soa.framework.leaf.service.GlobalIdService;

@Getter
public class GlobalIdServiceImpl implements GlobalIdService
{
	@Autowired(required = false)
	@Qualifier(LEAF_SNOWFLAKE_ID_GENERATOR_BEAN)
	IdGen leafSnowflakeIDGenerator;

	@Autowired(required = false)
	@Qualifier(LEAF_SEGMENT_ID_GENERATOR_BEAN)
	IdGen leafSegmentIDGenerator;

	@Override
	public long getSnowflakeID()
	{
		Result rs = leafSnowflakeIDGenerator.get(null);
		
		return checkResult(rs);
	}
	
	@Override
	public long getSegmentID(String tag)
	{
		Result rs = leafSegmentIDGenerator.get(tag);
		
		return checkResult(rs);
	}

	@Override
	public long getUniqueSegmentID()
	{
		return getSegmentID(UNIQUE_SEGMENT_TAG);
	}
	
	@Override
	public int getIntSegmentID(String tag)
	{
		long id = getSegmentID(tag);
		checkIntId(id);
		
		return (int)(id);
	}

	@Override
	public long[] getBatchSnowflakeID(int batchSize)
	{
		BatchResult brs = leafSnowflakeIDGenerator.getBatch(null, batchSize);
		
		return checkBatchResult(brs);
	}
	
	@Override
	public long[] getBatchSegmentID(String tag, int batchSize)
	{
		BatchResult brs = leafSegmentIDGenerator.getBatch(tag, batchSize);
		
		return checkBatchResult(brs);
	}
	
	@Override
	public long[] getBatchUniqueSegmentID(int batchSize)
	{
		return getBatchSegmentID(UNIQUE_SEGMENT_TAG, batchSize);
	}
	
	@Override
	public int[] getBatchIntSegmentID(String tag, int batchSize)
	{
		long[] ids   = getBatchSegmentID(tag, batchSize);
		int[] intIds = new int[ids.length];
		
		if(ids.length > 0)
		{
			checkIntId(ids[ids.length - 1]);
			
			for(int i = 0; i < ids.length; i++)
				intIds[i] = (int)(ids[i]);
		}
		
		return intIds;
	}
	
	@Override
	public SnowflakeID decodeSnowflakeId(long snowflakeId)
	{
		return decodeSnowflakeId(snowflakeId, IdGen.DEFAULT_SNOWFLAKE_TWEPOCK);
	}
	
	@Override
	public SnowflakeID decodeSnowflakeId(long snowflakeId, long twepoch)
	{
		long originTimestamp = (snowflakeId >> 22) + twepoch;
		int workerId = (int)((snowflakeId >> 12) ^ (snowflakeId >> 22 << 10));
		int sequence = (int)(snowflakeId ^ (snowflakeId >> 12 << 12));
		
		return new SnowflakeID(originTimestamp, workerId, sequence);
	}

	private long checkResult(Result rs)
	{
		long id = rs.getId();
		
		if(rs.getStatus() != Status.SUCCESS)
			throw new ServiceException(String.format("generate global ID fail -> (code: %d)", id), ServiceException.INNER_API_CALL_FAIL);
			
		return id;
	}

	private long[] checkBatchResult(BatchResult brs)
	{
		long[] ids = brs.getIds();
		
		if(brs.getStatus() != Status.SUCCESS)
		{
			long code = (ids != null && ids.length > 0) ? ids[0] : -100;
			throw new ServiceException(String.format("generate batch global ID fail -> (code: %d)", code), ServiceException.INNER_API_CALL_FAIL);
		}
			
		return ids;
	}
	
	private void checkIntId(long id)
	{
		if(id > Integer.MAX_VALUE)
			throw new ServiceException(String.format("generate integer global ID fail -> (id: %d) greater than Integer.MAX_VALUE", id), ServiceException.INNER_API_CALL_FAIL);
	}

}
