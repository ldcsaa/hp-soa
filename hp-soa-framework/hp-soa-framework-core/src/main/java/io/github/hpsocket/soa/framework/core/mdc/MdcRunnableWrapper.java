package io.github.hpsocket.soa.framework.core.mdc;

import lombok.Getter;

/** <b>支持调用链跟踪的 {@linkplain Runnable} 包装类</b> */
@Getter
public class MdcRunnableWrapper extends MdcRunnable
{
	private Runnable r;

	public MdcRunnableWrapper(Runnable r)
	{
		this.r = r;
	}

	public MdcRunnableWrapper(Runnable r, MdcAttr mdcAttr)
	{
		super(mdcAttr);
		this.r = r;
	}

	@Override
	public void doRun()
	{
		r.run();
	}

	public static MdcRunnableWrapper of(Runnable r)
	{
		return new MdcRunnableWrapper(r);
	}
}
