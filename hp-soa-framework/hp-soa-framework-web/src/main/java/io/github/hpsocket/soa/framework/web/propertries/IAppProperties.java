package io.github.hpsocket.soa.framework.web.propertries;

public interface IAppProperties
{
	String getId();
	String getName();
	String getVersion();
	String getOrganization();
	String getOwner();
	
	int getCookieMaxAge();
	
	boolean isReadOnly();
}
