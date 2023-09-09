package io.github.hpsocket.soa.framework.core.mdc;

import java.util.concurrent.Callable;

import lombok.Getter;

/** <b>支持调用链跟踪的 {@linkplain Callable} 包装类</b> */
@Getter
public class MdcCallableWrapper<T> extends MdcCallable<T>
{
	private Callable<T> c;

	public MdcCallableWrapper(Callable<T> c)
	{
		this.c = c;
	}

	public MdcCallableWrapper(Callable<T> c, MdcAttr mdcAttr)
	{
		super(mdcAttr);
		this.c = c;
	}

	@Override
	public T doCall() throws Exception
	{
		return c.call();
	}

	public static <T> MdcCallableWrapper<T> of(Callable<T> c)
	{
		return new MdcCallableWrapper<T>(c);
	}
}
