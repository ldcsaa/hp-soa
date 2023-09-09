package io.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Consumer;

import lombok.Getter;
/** <b>支持调用链跟踪的 {@linkplain Consumer} 基类</b> */
@Getter
abstract public class MdcConsumer<T> implements Consumer<T>
{
	private final MdcAttr mdcAttr;

	abstract protected void doAccept(T t);

	public MdcConsumer()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcConsumer(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
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
