package org.hpsocket.soa.framework.core.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest<T>
{
	private int pageNumber	= 1;
	private int pageSize	= PageInfo.DEFAULT_PAGE_SIZE;
	private String orderBy;

	private T param;
}
