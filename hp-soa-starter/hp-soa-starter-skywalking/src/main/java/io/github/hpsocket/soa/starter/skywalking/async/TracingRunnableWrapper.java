package io.github.hpsocket.soa.starter.skywalking.async;

import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;

@TraceCrossThread
public class TracingRunnableWrapper extends RunnableWrapper
{
	private final MdcAttr mdcAttr;
	
	public TracingRunnableWrapper(Runnable r)
	{
		this(r, MdcAttr.fromMdc());
	}
	
	public TracingRunnableWrapper(Runnable r, MdcAttr mdcAttr)
	{
		super(r);
		
		this.mdcAttr = mdcAttr;
	}
	
    @Override
    public void run()
    {
		try
		{
			mdcAttr.putMdc();
			
			super.run();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
    }

    public static TracingRunnableWrapper of(Runnable r)
    {
        return new TracingRunnableWrapper(r);
    }
}
