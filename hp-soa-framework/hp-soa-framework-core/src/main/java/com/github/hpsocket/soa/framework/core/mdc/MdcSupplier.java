package com.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Supplier;

import lombok.Getter;

@Getter
abstract public class MdcSupplier<T> implements Supplier<T>
{
	private final MdcAttr mdcAttr;

	abstract protected T doGet();

	public MdcSupplier()
	{
		this.mdcAttr = MdcAttr.fromMdc();
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
