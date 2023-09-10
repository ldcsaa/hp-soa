package io.github.hpsocket.soa.framework.web.propertries;

/** <b>Servlet 相关路径配置接口</b><br>
 * 由 HP-SOA 框架实现，应用程序可注入该接口服务
 */
public interface IServletPathsPropertries
{
	String getServletContextPath();
	String getSpringMvcServletPath();
	String getManagementEndpointsBasePath();
	String getSpringdocApiDocsPath();
	String getSpringdocSwaggerUiPath();
}
