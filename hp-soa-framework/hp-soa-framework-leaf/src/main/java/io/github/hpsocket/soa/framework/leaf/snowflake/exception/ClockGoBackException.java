
package io.github.hpsocket.soa.framework.leaf.snowflake.exception;

@SuppressWarnings("serial")
public class ClockGoBackException extends RuntimeException
{
	public ClockGoBackException(String message)
	{
		super(message);
	}
}
