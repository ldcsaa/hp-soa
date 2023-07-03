
package org.hpsocket.soa.framework.core.paging;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
public class PageInfo implements Serializable
{
	public static final int DEFAULT_PAGE_SIZE = 20;

	private int rowCount	= 0;
	private int currentPage	= 1;
	private int pageSize	= DEFAULT_PAGE_SIZE;
	private int pageCount	= 0;
	private int totalCount	= 0;
	private String orderBy;

	public int calculatePageCount()
	{
		pageCount = totalCount / pageSize + ((totalCount % pageSize == 0) ? 0 : 1);
		
		if(currentPage < pageCount)
			rowCount = pageSize;
		else if(currentPage == pageCount)
			rowCount = totalCount - (pageCount - 1) * pageSize;
		
		return pageCount;
	}
	
	public boolean outofBounds()
	{
		return currentPage > pageCount || currentPage < 1;
	}
	
	public int limitStart()
	{
		return (currentPage - 1) * pageSize;
	}

}
