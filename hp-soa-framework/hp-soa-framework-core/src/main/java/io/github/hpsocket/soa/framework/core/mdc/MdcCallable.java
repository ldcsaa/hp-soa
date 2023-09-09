package io.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.Callable;

import lombok.Getter;

/** <b>支持调用链跟踪的 {@linkplain Callable} 基类</b> */
@Getter
abstract public class MdcCallable<T> implements Callable<T>
{
	private MdcAttr mdcAttr;
	
	abstract protected T doCall() throws Exception;

	public MdcCallable()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcCallable(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
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
