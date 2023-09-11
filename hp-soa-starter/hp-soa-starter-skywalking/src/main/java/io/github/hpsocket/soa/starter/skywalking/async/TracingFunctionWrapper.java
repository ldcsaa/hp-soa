package io.github.hpsocket.soa.starter.skywalking.async;

import java.util.function.Function;

import org.apache.skywalking.apm.toolkit.trace.FunctionWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;

/** <b>{@linkplain Function} 包装类（注入 {@linkplain org.slf4j.MDC MDC} 和 traceId 调用链跟踪信息）</b> */
@TraceCrossThread
public class TracingFunctionWrapper<T, R> extends FunctionWrapper<T, R>
{
	private final MdcAttr mdcAttr;
	
	public TracingFunctionWrapper(Function<T, R> f)
	{
		this(f, MdcAttr.fromMdc());
	}
	
	public TracingFunctionWrapper(Function<T, R> f, MdcAttr mdcAttr)
	{
		super(f);
		
		this.mdcAttr = mdcAttr;
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
