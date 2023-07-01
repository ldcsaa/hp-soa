package org.hpsocket.soa.framework.core.thread;

import java.util.concurrent.RecursiveAction;

import org.hpsocket.soa.framework.core.mdc.MdcAttr;

@SuppressWarnings("serial")
abstract public class MdcRecursiveAction extends RecursiveAction
{
	private MdcAttr mdcAttr;
	
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
