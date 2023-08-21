package io.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class MdcConsumerWrapper<T> extends MdcConsumer<T>
{
	private Consumer<T> c;

	public MdcConsumerWrapper(Consumer<T> c)
	{
		this.c = c;
	}
	
	public MdcConsumerWrapper(Consumer<T> c, MdcAttr mdcAttr)
	{
		super(mdcAttr);
		this.c = c;
	}
	
	@Override
	public void doAccept(T t)
	{
		c.accept(t);
	}

	public static <T> MdcConsumerWrapper<T> of(Consumer<T> c)
	{
		return new MdcConsumerWrapper<T>(c);
	}
}
