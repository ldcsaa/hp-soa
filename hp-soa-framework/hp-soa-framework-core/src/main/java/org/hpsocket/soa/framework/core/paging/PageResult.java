package org.hpsocket.soa.framework.core.paging;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T>
{
	private PageInfo page;
	private List<T> data;
}
