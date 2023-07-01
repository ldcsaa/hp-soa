package org.hpsocket.soa.framework.core.thread;

import java.util.concurrent.RecursiveTask;

import org.hpsocket.soa.framework.core.mdc.MdcAttr;

@SuppressWarnings("serial")
abstract public class MdcRecursiveTask<T> extends RecursiveTask<T>
{
	private MdcAttr mdcAttr;
	
	abstract protected T doCompute();

	public MdcRecursiveTask()
	{
		this.mdcAttr = MdcAttr.fromMdc();
	}
	
	@Override
	protected T compute()
	{
		try
		{
			mdcAttr.putMdc();
			
			return doCompute();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}
	
}
