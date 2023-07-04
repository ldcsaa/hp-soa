package org.hpsocket.soa.framework.web.service;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface AsyncService
{
	void runAsync(Runnable task);
	void invokeAsync(final Object obj, final Method method, final Object ... args);
	void execute(Runnable task);
	Future<?> submit(Runnable task);
	<T> Future<T> submit(Callable<T> task);
	<T> Future<T> submit(Runnable task, T result);
}
