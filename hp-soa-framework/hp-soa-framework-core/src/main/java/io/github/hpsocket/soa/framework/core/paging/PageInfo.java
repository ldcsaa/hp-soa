
package io.github.hpsocket.soa.framework.core.paging;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <b>通用分页信息</b> */
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
public class PageInfo implements Serializable
{
	public static final int DEFAULT_PAGE_SIZE = 20;

	private int pageRows	= 0;
	private int pageNumber	= 1;
	private int pageSize	= DEFAULT_PAGE_SIZE;
	private int pageCount	= 0;
	private int totalRows	= 0;

	public int calculatePageCount()
	{
		pageCount = totalRows / pageSize + ((totalRows % pageSize == 0) ? 0 : 1);
		
		if(pageNumber < pageCount)
			pageRows = pageSize;
		else if(pageNumber == pageCount)
			pageRows = totalRows - (pageCount - 1) * pageSize;
		
		return pageCount;
	}
	
	public boolean outofBounds()
	{
		return pageNumber > pageCount || pageNumber < 1;
	}
	
	public int limitStart()
	{
		return (pageNumber - 1) * pageSize;
	}

}
