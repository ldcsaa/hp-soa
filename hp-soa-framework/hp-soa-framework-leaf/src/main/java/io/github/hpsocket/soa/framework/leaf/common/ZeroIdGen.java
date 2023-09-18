
package io.github.hpsocket.soa.framework.leaf.common;

import io.github.hpsocket.soa.framework.leaf.service.IdGen;

public class ZeroIdGen implements IdGen
{
	@Override
	public Result get(String key)
	{
		return new Result(0, Status.SUCCESS);
	}

	@Override
	public boolean init()
	{
		return true;
	}
}
