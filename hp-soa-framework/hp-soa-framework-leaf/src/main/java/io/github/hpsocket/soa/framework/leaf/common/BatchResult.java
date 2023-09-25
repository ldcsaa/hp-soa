
package io.github.hpsocket.soa.framework.leaf.common;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchResult
{
    public static final int MAX_BATCH_SIZE = 2000;

    private long[] ids;
    private Status status;

    public static void checkBatchSize(int size)
    {
        if(size > MAX_BATCH_SIZE)
            throw new IllegalArgumentException(String.format("batch size arg (%d) is greater than max batch size limit (%d)", size, MAX_BATCH_SIZE));
        if(size <= 0)
            throw new IllegalArgumentException(String.format("batch size arg (%d) is less than or equal to 0", size));
    }

    public static BatchResult exceptionResult(long code, int size)
    {
        long[] ids = new long[size];
        return exceptionResult(ids, code);
    }

    public static BatchResult exceptionResult(long[] ids, long code)
    {
        if(ids != null && ids.length > 0)
            ids[0] = code;

        return new BatchResult(ids, Status.EXCEPTION);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        return sb.append("{status=").append(status).append(", ids=").append(Arrays.toString(ids)).append('}').toString();
    }
}
