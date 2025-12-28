package io.github.hpsocket.soa.framework.core.paging;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <b>通用分页结果</b> */
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
public class PageResult<T> implements Serializable
{
    private PageInfo pageInfo;
    private List<? extends T> list;

    public PageResult(PageInfo pageInfo, List<? extends T> list)
    {
        int pageRows = pageInfo.getPageRows();
        int listSize = list.size();

        if(pageRows != listSize)
            throw new IllegalArgumentException(String.format("page rows (%d) does not match list size (%d)", pageRows, listSize));

        this.pageInfo = pageInfo;
        this.list     = list;
    }

    public PageResult(int pageNumber, int pageSize, int totalRows, List<? extends T> list)
    {
        this(new PageInfo(pageNumber, pageSize, totalRows), list);
    }

}
