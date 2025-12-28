
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

    private int pageNumber  = 1;
    private int pageSize    = DEFAULT_PAGE_SIZE;
    private int totalRows   = 0;
    private int pageCount   = 0;
    private int pageRows    = 0;

    public PageInfo(int pageNumber, int pageSize, int totalRows)
    {
        this.pageNumber = pageNumber;
        this.pageSize   = pageSize;
        this.totalRows  = totalRows;

        calculate();
    }

    public boolean isOutOfBounds()
    {
        return pageNumber > pageCount || pageNumber < 1;
    }
    
    public int getStartIndex()
    {
        return (pageNumber - 1) * pageSize;
    }

    public int getEndIndex()
    {
        return pageNumber * pageSize;
    }

    private void calculate()
    {
        pageCount = totalRows / pageSize + ((totalRows % pageSize == 0) ? 0 : 1);

        if(pageNumber < pageCount)
            pageRows = pageSize;
        else if(pageNumber == pageCount)
            pageRows = totalRows - (pageCount - 1) * pageSize;
        else
            pageRows = 0;
    }

}
