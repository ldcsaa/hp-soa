
package io.github.hpsocket.soa.framework.leaf.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result
{
	private long id;
	private Status status;

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		return sb.append("{status=").append(status).append(", id=").append(id).append('}').toString();
	}
}
