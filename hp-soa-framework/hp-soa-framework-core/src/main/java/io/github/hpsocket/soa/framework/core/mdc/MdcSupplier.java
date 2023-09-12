package io.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Supplier;

import lombok.Getter;

/** <b>{@linkplain Supplier} 基类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
@Getter
abstract public class MdcSupplier<T> implements Supplier<T>
{
	private final MdcAttr mdcAttr;

	abstract protected T doGet();

	public MdcSupplier()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcSupplier(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
	}
	
	@Override
	public T get()
	{
		try
		{
			mdcAttr.putMdc();
			
			return doGet();
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}

}
