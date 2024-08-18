
package io.github.hpsocket.soa.framework.web.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.json.JSONFieldExclude;
import io.github.hpsocket.soa.framework.web.json.JSONFieldExclude.Exclude;

import lombok.Getter;
import lombok.Setter;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/** <b>HTTP 请求统一响应对象</b> */
@Getter
@Setter
@SuppressWarnings("serial")
@Schema(description="接口方法返回值对象")
public class Response<T> implements Serializable
{
    public static final String MSG_OK       = "ok";
    /** 登录标识 */
    public static final Integer RT_LOGIN    = Integer.valueOf(1);
    /** 登出标识 */
    public static final Integer RT_LOGOUT   = Integer.valueOf(2);

    /** 状态码：应用程序可自定义状态码，系统状态码参考 {@linkplain ServiceException} */
    @Schema(title="状态码", example="0", requiredMode=RequiredMode.REQUIRED, nullable=false)
    private Integer statusCode = OK;
    
    /** 结果码：用于服务内部监控、统计，不暴露到调用方，应用程序可自定义结果码，系统结果码参考 {@linkplain ServiceException} */
    //@JsonIgnore
    //@JSONField(serialize = false)
    @Schema(title="业务处理代码", example="0", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private transient Integer resultCode = OK;
    
    /** 状态描述 */
    @Schema(title="状态描述", example=MSG_OK, requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private String msg = MSG_OK;
    
    /** 服务端处理耗时（毫秒） */
    @Schema(title="耗时（毫秒）", example="456", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private long costTime;

    /** 参数校验错误列表 */
    //@JsonInclude(Include.NON_NULL)
    @JSONFieldExclude(Exclude.NULL)
    @Schema(title="参数校验错误列表", example="name is empty", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private Map<String, List<String>> validationErrors;
    
    /** 请求ID */
    //@JsonInclude(Include.NON_NULL)
    @JSONFieldExclude(Exclude.NULL)
    @Schema(title="请求ID", example="def7866fb25cb84a7652ed5ba9974102", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private String requestId;
    
    /** 业务模型对象 */
    @Schema(title="业务模型对象", example="Any Object", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private T result;
    
    /** 响应类型（目前仅用于登录登出操作：{@linkplain #RT_LOGIN} - 登录，{@linkplain #RT_LOGOUT} - 登出） */
    //@JsonIgnore
    //@JSONField(serialize = false)
    @Schema(title="响应类型（1 - 登录 - 2：登出）", example="null", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
    private transient Integer respType;

    public Response()
    {

    }

    public Response(T result)
    {
        this.result = result;
    }

    public Response(String msg, Integer statusCode)
    {
        this.msg = msg;
        this.statusCode = statusCode;
    }

    public Response(ServiceException e)
    {
        this(e.getMessage(), e.getStatusCode(), e.getResultCode());
    }

    public Response(Map<String, List<String>> validationErrors)
    {
        this(PARAM_VERIFY_EXCEPTION, validationErrors);
    }

    public Response(ServiceException e, Map<String, List<String>> validationErrors)
    {
        this(e);
        this.validationErrors = validationErrors;
    }

    public Response(String msg, Integer statusCode, Integer resultCode)
    {
        this.msg = msg;
        this.statusCode = statusCode;
        this.resultCode = resultCode;
    }

}
