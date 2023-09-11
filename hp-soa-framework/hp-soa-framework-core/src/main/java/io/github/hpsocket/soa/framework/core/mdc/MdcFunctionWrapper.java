package io.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Function;

import lombok.Getter;

/** <b>{@linkplain Function} 包装类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
@Getter
public class MdcFunctionWrapper<T, R> extends MdcFunction<T, R>
{
	private Function<T, R> f;

	public MdcFunctionWrapper(Function<T, R> f)
	{
		this.f = f;
	}
	
	public MdcFunctionWrapper(Function<T, R> f, MdcAttr mdcAttr)
	{
		super(mdcAttr);
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