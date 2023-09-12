package io.github.hpsocket.soa.framework.core.mdc;

import lombok.Getter;

/** <b>{@linkplain Runnable} 基类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
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
