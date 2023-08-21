package io.github.hpsocket.soa.framework.web.service;

public interface TracingContext
{
	String getTraceId();
	String getSpanId();
}
