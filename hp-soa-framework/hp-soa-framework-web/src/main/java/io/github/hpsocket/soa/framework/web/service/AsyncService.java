package io.github.hpsocket.soa.framework.web.service;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/** <b>异步服务接口</b><br>
 * 由 HP-SOA 框架实现，该服务支持调用链跟踪，应用程序可注入该接口服务
 */
public interface AsyncService
{
	/** 异步执行 {@linkplain Runnable} */
	void runAsync(Runnable task);
	/** 异步调用某个对象的方法 */
	void invokeAsync(final Object obj, final Method method, final Object ... args);
	/** 异步执行 {@linkplain Runnable} */
	void execute(Runnable task);
	/** 异步提交 {@linkplain Runnable} */
	Future<?> submit(Runnable task);
	/** 异步提交 {@linkplain Callable} */
	<T> Future<T> submit(Callable<T> task);
	/** 异步提交 {@linkplain Runnable} */
	<T> Future<T> submit(Runnable task, T result);
}
