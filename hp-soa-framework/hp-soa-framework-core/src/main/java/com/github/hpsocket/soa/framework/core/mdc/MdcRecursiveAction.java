package com.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.RecursiveAction;

import lombok.Getter;

@Getter
@SuppressWarnings("serial")
abstract public class MdcRecursiveAction extends RecursiveAction
{
	private final MdcAttr mdcAttr;
	
	abstract protected void doCompute();

	public MdcRecursiveAction()
	{
		mdcAttr = MdcAttr.fromMdc();
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
