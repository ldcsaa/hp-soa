package com.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Supplier;

import lombok.Getter;

@Getter
public class MdcSupplierWrapper<T> extends MdcSupplier<T>
{
	private Supplier<T> s;

	public MdcSupplierWrapper(Supplier<T> s)
	{
		this.s = s;
	}
	
	public MdcSupplierWrapper(Supplier<T> s, MdcAttr mdcAttr)
	{
		super(mdcAttr);
		this.s = s;
	}
	
	@Override
	public T doGet()
	{
		return s.get();
	}

	public static <T> MdcSupplierWrapper<T> of(Supplier<T> s)
	{
		return new MdcSupplierWrapper<T>(s);
	}

}
