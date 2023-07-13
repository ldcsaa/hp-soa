package com.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.Callable;

import lombok.Getter;

@Getter
abstract public class MdcCallable<T> implements Callable<T>
{
	private MdcAttr mdcAttr;
	
	abstract protected T doCall();

	public MdcCallable()
	{
		mdcAttr = MdcAttr.fromMdc();
	}

	@Override
	public T call() throws Exception
	{
		try
		{
			mdcAttr.putMdc();
			
			return doCall();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}

}
