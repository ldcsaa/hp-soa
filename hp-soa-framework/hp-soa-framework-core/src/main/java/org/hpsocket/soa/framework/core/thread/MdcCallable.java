package org.hpsocket.soa.framework.core.thread;

import java.util.concurrent.Callable;

import org.hpsocket.soa.framework.core.mdc.MdcAttr;

abstract public class MdcCallable<T> extends MdcAttr implements Callable<T>
{
	abstract protected T doCall();

	public MdcCallable()
	{

	}

	@Override
	public T call() throws Exception
	{
		try
		{
			putMdc();
			return doCall();
		}
		finally
		{
			removeMdc();
		}
	}

}
