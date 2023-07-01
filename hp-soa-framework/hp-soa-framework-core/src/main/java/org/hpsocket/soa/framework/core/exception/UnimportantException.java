
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
		setResultCode(API_OK);
	}

	public UnimportantException(String message, int statusCode)
	{
		super(message, statusCode);
		setResultCode(API_OK);
	}

	public UnimportantException(String message, Throwable cause)
	{
		super(message, cause);
		setResultCode(API_OK);
	}

	public UnimportantException(String message, int statusCode, Throwable cause)
	{
		super(message, statusCode, cause);
		setResultCode(API_OK);
	}
}
