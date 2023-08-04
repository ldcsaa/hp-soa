package com.github.hpsocket.soa.starter.skywalking.async;

import java.util.function.Function;

import org.apache.skywalking.apm.toolkit.trace.FunctionWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;

@TraceCrossThread
public class TracingFunctionWrapper<T, R> extends FunctionWrapper<T, R>
{
	private final MdcAttr mdcAttr;
	
	public TracingFunctionWrapper(Function<T, R> f)
	{
		super(f);
		
		mdcAttr = MdcAttr.fromMdc();
	}
	
    @Override
    public R apply(T t)
    {
		try
		{
			mdcAttr.putMdc();
			
			return super.apply(t);
		}
		finally
		{
			mdcAttr.removeMdc();
		}
    }

    public static <T, R> TracingFunctionWrapper<T, R> of(Function<T, R> f)
    {
        return new TracingFunctionWrapper<>(f);
    }

}
