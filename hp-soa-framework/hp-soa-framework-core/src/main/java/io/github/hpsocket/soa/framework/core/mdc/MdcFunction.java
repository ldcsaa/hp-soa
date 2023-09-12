package io.github.hpsocket.soa.framework.core.mdc;

import java.util.function.Function;

import lombok.Getter;

/** <b>{@linkplain Function} 基类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
@Getter
abstract public class MdcFunction<T, R> implements Function<T, R>
{
	private final MdcAttr mdcAttr;

	abstract protected R doApply(T t);

	public MdcFunction()
	{
		this(MdcAttr.fromMdc());
	}

	public MdcFunction(MdcAttr mdcAttr)
	{
		this.mdcAttr = mdcAttr;
	}
	
	@Override
	public R apply(T t)
	{
		try
		{
			mdcAttr.putMdc();
			
			return doApply(t);
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}
}