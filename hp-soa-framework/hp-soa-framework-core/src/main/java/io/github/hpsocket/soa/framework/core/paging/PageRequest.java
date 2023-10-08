package io.github.hpsocket.soa.framework.core.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <b>通用分页请求</b> */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest<T>
{
    private int pageNumber = 1;
    private int pageSize   = PageInfo.DEFAULT_PAGE_SIZE;
    private String orderBy;

    private T param;
}
