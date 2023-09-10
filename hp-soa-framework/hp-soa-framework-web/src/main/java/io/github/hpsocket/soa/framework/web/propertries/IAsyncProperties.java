package io.github.hpsocket.soa.framework.web.propertries;

/** <b>异步配置接口</b><br>
 * 由 HP-SOA 框架实现，应用程序可注入该接口服务<br>
 * 由 <i>${hp.soa.web.async}</i> 配置项提供配置值
 */
public interface IAsyncProperties
{
	int getCorePoolSize();
	int getMaxPoolSize();
	int getKeepAliveSeconds();
	int getQueueCapacity();
	String getRejectionPolicy();
	boolean isAllowCoreThreadTimeOut();
}
