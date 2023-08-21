package io.github.hpsocket.soa.framework.web.propertries;

public interface IAsyncProperties
{
	int getCorePoolSize();
	int getMaxPoolSize();
	int getKeepAliveSeconds();
	int getQueueCapacity();
	String getRejectionPolicy();
	boolean isAllowCoreThreadTimeOut();
}
