package org.hpsocket.soa.framework.core.thread;

import org.hpsocket.soa.framework.core.mdc.MdcAttr;

abstract public class MdcRunnable extends MdcAttr implements Runnable
{
	abstract protected void doRun();

	public MdcRunnable()
	{
		
	}

	@Override
	public void run()
	{
		try
		{
			putMdc();
			doRun();
		}
		finally
		{
			removeMdc();
		}
	}

}
