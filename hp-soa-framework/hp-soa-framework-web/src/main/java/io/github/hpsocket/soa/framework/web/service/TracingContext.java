package io.github.hpsocket.soa.framework.web.service;

/** <b>调用链上下文接口</b><br>
 * 由 HP-SOA 框架实现，应用程序可注入该接口服务
 */
public interface TracingContext
{
    /** 获取调用链 traceId */
    String getTraceId();
    /** 获取调用链 spanId */
    String getSpanId();
}
