package io.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.RecursiveTask;

import lombok.Getter;

/** <b>{@linkplain RecursiveTask} 基类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
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
