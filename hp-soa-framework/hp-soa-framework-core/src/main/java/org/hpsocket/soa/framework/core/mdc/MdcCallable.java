package org.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.Callable;

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
