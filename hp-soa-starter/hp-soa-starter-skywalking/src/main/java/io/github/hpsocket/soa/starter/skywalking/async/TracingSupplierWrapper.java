package io.github.hpsocket.soa.starter.skywalking.async;

import java.util.function.Supplier;

import org.apache.skywalking.apm.toolkit.trace.SupplierWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;

/** <b>{@linkplain Supplier} 包装类（注入 {@linkplain org.slf4j.MDC MDC} 和 traceId 调用链跟踪信息）</b> */
@TraceCrossThread
public class TracingSupplierWrapper<V> extends SupplierWrapper<V>
{
	private final MdcAttr mdcAttr;
	
	public TracingSupplierWrapper(Supplier<V> s)
	{
		this(s, MdcAttr.fromMdc());
	}

	public TracingSupplierWrapper(Supplier<V> s, MdcAttr mdcAttr)
	{
		super(s);
		
		this.mdcAttr = mdcAttr;
	}

    @Override
    public V get()
    {
		try
		{
			mdcAttr.putMdc();
			
			return super.get();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
    }

    public static <V> TracingSupplierWrapper<V> of(Supplier<V> s)
    {
        return new TracingSupplierWrapper<>(s);
    }
}
