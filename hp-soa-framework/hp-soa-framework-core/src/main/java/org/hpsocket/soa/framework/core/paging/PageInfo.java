
package org.hpsocket.soa.framework.core.paging;

import java.io.Serializable;

import com.alibaba.fastjson2.annotation.JSONField;


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
	
	@JSONField(serialize = false)
	public boolean isOutofBounds()
	{
		return currentPage > pageCount || currentPage < 1;
	}
	
	@JSONField(serialize = false)
	public int getLimitStart()
	{
		return (currentPage - 1) * pageSize;
	}

	public int getRowCount()
	{
		return rowCount;
	}

	public void setRowCount(int rowCount)
	{
		this.rowCount = rowCount;
	}

	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getPageCount()
	{
		return pageCount;
	}

	public void setPageCount(int pageCount)
	{
		this.pageCount = pageCount;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(int totalCount)
	{
		this.totalCount = totalCount;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

}
