
package io.github.hpsocket.soa.framework.core.exception;

/** <b>HP-SOA 统一非严重异常，即使引发异常也认为调用成功</b><br>
 * {@linkplain UnimportantException#getResultCode() getResultCode()} 总是返回 {@linkplain ServiceException#OK OK}<br>
 * 如：用户输错密码导致登录失败，触发 {@linkplain ServiceException#LOGIN_INVALID_EXCEPTION LOGIN_INVALID_EXCEPTION}，用户调用是失败的，但服务内部处理是成功的。因此监控系统也会把该请求看作成功请求
 */
@SuppressWarnings("serial")
public class UnimportantException extends ServiceException
{
	public UnimportantException()
	{
		setResultCode(OK);
	}

	public UnimportantException(String message)
	{
		super(message);
		setResultCode(OK);
	}

	public UnimportantException(String message, Integer statusCode)
	{
		super(message, statusCode);
		setResultCode(OK);
	}

	public UnimportantException(String message, Throwable cause)
	{
		super(message, cause);
		setResultCode(OK);
	}

	public UnimportantException(String message, Integer statusCode, Throwable cause)
	{
		super(message, statusCode, cause);
		setResultCode(OK);
	}
}
