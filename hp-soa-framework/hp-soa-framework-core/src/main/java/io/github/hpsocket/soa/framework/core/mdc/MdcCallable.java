package io.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.Callable;

import lombok.Getter;

/** <b>{@linkplain Callable} 基类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
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
