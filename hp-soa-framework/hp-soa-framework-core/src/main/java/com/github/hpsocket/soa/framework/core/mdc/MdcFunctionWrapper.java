package com.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Function;

import lombok.Getter;

@Getter
public class MdcFunctionWrapper<T, R> extends MdcFunction<T, R>
{
	private Function<T, R> f;

	public MdcFunctionWrapper(Function<T, R> f)
	{
		this.f = f;
	}
	
	@Override
	public R doApply(T t)
	{
		return f.apply(t);
	}

	public static <T, R> MdcFunctionWrapper<T, R> of(Function<T, R> f)
	{
		return new MdcFunctionWrapper<T, R>(f);
	}
}