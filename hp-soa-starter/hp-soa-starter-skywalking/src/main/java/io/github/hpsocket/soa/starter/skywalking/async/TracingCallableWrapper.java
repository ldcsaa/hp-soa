package io.github.hpsocket.soa.starter.skywalking.async;

import java.util.concurrent.Callable;

import org.apache.skywalking.apm.toolkit.trace.CallableWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;

/** <b>{@linkplain Callable} 包装类（注入 {@linkplain org.slf4j.MDC MDC} 和 traceId 调用链跟踪信息）</b> */
@TraceCrossThread
public class TracingCallableWrapper<V> extends CallableWrapper<V>
{
	private final MdcAttr mdcAttr;
	
	public TracingCallableWrapper(Callable<V> c)
	{
		this(c, MdcAttr.fromMdc());
	}
	
 	public TracingCallableWrapper(Callable<V> c, MdcAttr mdcAttr)
	{
		super(c);
		
		this.mdcAttr = mdcAttr;
	}
	
   @Override
    public V call() throws Exception
    {
		try
		{
			mdcAttr.putMdc();
			
			return super.call();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
    }

    public static <V> TracingCallableWrapper<V> of(Callable<V> r)
    {
        return new TracingCallableWrapper<>(r);
    }
}
