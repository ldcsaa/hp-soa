
package com.github.hpsocket.soa.framework.core.exception;

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
