package io.github.hpsocket.soa.framework.core.paging;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <b>通用分页结果</b> */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T>
{
	private PageInfo page;
	private List<T> data;
}
