package io.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.RecursiveAction;

import lombok.Getter;

/** <b>支持调用链跟踪的 {@linkplain RecursiveAction} 基类</b> */
@Getter
@SuppressWarnings("serial")
abstract public class MdcRecursiveAction extends RecursiveAction
{
	private final MdcAttr mdcAttr;
	
	abstract protected void doCompute();

	public MdcRecursiveAction()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcRecursiveAction(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
	}
	
	@Override
	protected void compute()
	{
		try
		{
			mdcAttr.putMdc();
			
			doCompute();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}
	
}
