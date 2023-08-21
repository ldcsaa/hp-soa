package io.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.RecursiveTask;

import lombok.Getter;

@Getter
@SuppressWarnings("serial")
abstract public class MdcRecursiveTask<T> extends RecursiveTask<T>
{
	private final MdcAttr mdcAttr;
	
	abstract protected T doCompute();

	public MdcRecursiveTask()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcRecursiveTask(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
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
