package io.github.hpsocket.soa.framework.web.propertries;

/** <b>应用程序基本属性接口</b><br>
 * 由 HP-SOA 框架实现，应用程序可注入该接口服务
 */
public interface IAppProperties
{
	/** 获取应用程序 ID */
	String getId();
	/** 获取应用程序名称 */
	String getName();
	/** 获取应用程序版本 */
	String getVersion();
	/** 获取应用程序所在组织 */
	String getOrganization();
	/** 获取应用程序拥有者 */
	String getOwner();
	
	/** 获取应用程序默认 Cookie 最大生命周期 */
	int getCookieMaxAge();
	
	/** 检测应用程序是否只读 */
	boolean isReadOnly();
}
