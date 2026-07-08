package io.github.hpsocket.soa.framework.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Bruce
 * @date 2026/7/1 22:14
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页查询请求对象")
public class PageQueryReq<T> implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;

    @Schema(description = "页码", example = "1")
    private Integer pageNum  = DEFAULT_PAGE_NUM;
    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @Schema(description = "排序类型（由接口提供方指定字段含义）", example = "1")
    private Integer orderBy;

    @Schema(description = "查询参数", required = true)
    @NotNull
    private T param;

    @Schema(description = "开始记录索引", hidden = true)
    public Integer getStartIndex()
    {
        if(pageNum == null || pageSize == null)
            return null;

        return (pageNum - 1) * pageSize;
    }
}
