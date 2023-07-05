package org.hpsocket.soa.framework.core.mdc;

abstract public class MdcRunnable implements Runnable
{
	private MdcAttr mdcAttr;

	abstract protected void doRun();

	public MdcRunnable()
	{
		this.mdcAttr = MdcAttr.fromMdc();
	}

	@Override
	public void run()
	{
		try
		{
			mdcAttr.putMdc();
			
			doRun();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}

}
