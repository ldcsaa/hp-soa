
package io.github.hpsocket.soa.framework.leaf.service;

import io.github.hpsocket.soa.framework.leaf.common.BatchResult;
import io.github.hpsocket.soa.framework.leaf.common.Result;
import io.github.hpsocket.soa.framework.leaf.common.Status;

public interface IdGen
{
    final long DEFAULT_SNOWFLAKE_TWEPOCK = 1688140800000L;

    Result get(String key);

    boolean init();

    default BatchResult getBatch(String key, int size)
    {
        BatchResult.checkBatchSize(size);

        long[] ids = new long[size];
        
        for(int i = 0; i < size; i++)
        {
            Result rs = get(key);
            
            if(rs.getStatus() == Status.SUCCESS)
                ids[i] = rs.getId();
            else
            {
                return BatchResult.exceptionResult(ids, rs.getId());
            }
        }

        return new BatchResult(ids, Status.SUCCESS);
    }

}
