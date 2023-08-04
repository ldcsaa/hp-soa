package com.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Consumer;

import lombok.Getter;

@Getter
abstract public class MdcConsumer<T> implements Consumer<T>
{
	private final MdcAttr mdcAttr;

	abstract protected void doAccept(T t);

	public MdcConsumer()
	{
		this.mdcAttr = MdcAttr.fromMdc();
	}
	
	@Override
	public void accept(T t)
	{
		try
		{
			mdcAttr.putMdc();
			
			doAccept(t);
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}

}
