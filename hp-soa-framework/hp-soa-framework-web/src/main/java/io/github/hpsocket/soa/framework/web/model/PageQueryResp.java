package io.github.hpsocket.soa.framework.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Bruce
 * @date 2026/7/1 22:14
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页查询响应对象")
public class PageQueryResp<T> implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码")
    private Integer pageNum;
    @Schema(description = "每页数量")
    private Integer pageSize;
    @Schema(description = "总页数")
    private Integer pageCount;
    @Schema(description = "当前页记录数")
    private Integer pageRows;
    @Schema(description = "总记录数")
    private Integer totalRows;
    @Schema(description = "当前页记录集")
    private List<T> records;

    public PageQueryResp(List<T> records, int pageNum, int pageSize, int totalRows)
    {
        this.records = records;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalRows = totalRows;
        this.pageRows = records.size();
        this.pageCount = (totalRows / pageSize + (totalRows % pageSize == 0 ? 0 : 1));
    }

    public static <T> PageQueryResp<T> of(List<T> records, int pageNum, int pageSize, int totalRows)
    {
        return new PageQueryResp<>(records, pageNum, pageSize, totalRows);
    }
}
