package io.github.hpsocket.soa.framework.core.mdc;

import lombok.Getter;

@Getter
abstract public class MdcRunnable implements Runnable
{
	private final MdcAttr mdcAttr;

	abstract protected void doRun();

	public MdcRunnable()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcRunnable(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
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
