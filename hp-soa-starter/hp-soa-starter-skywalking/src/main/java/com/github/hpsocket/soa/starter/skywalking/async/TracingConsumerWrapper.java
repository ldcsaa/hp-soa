package com.github.hpsocket.soa.starter.skywalking.async;

import java.util.function.Consumer;

import org.apache.skywalking.apm.toolkit.trace.ConsumerWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;

@TraceCrossThread
public class TracingConsumerWrapper<V> extends ConsumerWrapper<V>
{
	private final MdcAttr mdcAttr;
	
	public TracingConsumerWrapper(Consumer<V> c)
	{
		super(c);
		
		mdcAttr = MdcAttr.fromMdc();
	}
	
	@Override
	public void accept(V v)
	{		
		try
		{
			mdcAttr.putMdc();
			
			super.accept(v);
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}

    public static <V> TracingConsumerWrapper<V> of(Consumer<V> c)
    {
        return new TracingConsumerWrapper<>(c);
    }

}
