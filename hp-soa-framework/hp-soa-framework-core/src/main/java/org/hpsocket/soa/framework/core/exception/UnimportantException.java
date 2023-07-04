
package org.hpsocket.soa.framework.core.exception;

@SuppressWarnings("serial")
public class UnimportantException extends ServiceException
{
	public UnimportantException()
	{
	}

	public UnimportantException(String message)
	{
		super(message);
		setResultCode(OK);
	}

	public UnimportantException(String message, int statusCode)
	{
		super(message, statusCode);
		setResultCode(OK);
	}

	public UnimportantException(String message, Throwable cause)
	{
		super(message, cause);
		setResultCode(OK);
	}

	public UnimportantException(String message, int statusCode, Throwable cause)
	{
		super(message, statusCode, cause);
		setResultCode(OK);
	}
}
