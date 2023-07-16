
package com.github.hpsocket.soa.framework.web.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.hpsocket.soa.framework.core.exception.ServiceException;

import lombok.Getter;
import lombok.Setter;

import static com.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Getter
@Setter
@SuppressWarnings("serial")
@Schema(description="接口方法返回值对象")
public class Response<T> implements Serializable
{
	public static final String MSG_OK		= "ok";	
	public static final Integer RT_LOGIN	= Integer.valueOf(1);
	public static final Integer RT_LOGOUT	= Integer.valueOf(2);

	@Schema(title="状态码", example="200", requiredMode=RequiredMode.REQUIRED, nullable=false)
	private Integer statusCode = OK;
	
	@JsonIgnore
	@Schema(title="业务处理代码", example="1000", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
	private Integer resultCode = OK;
	
	@Schema(title="状态描述", example=MSG_OK, requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
	private String msg = MSG_OK;
	
	@Schema(title="耗时（毫秒）", example="456", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
	private long costTime;
	
	@JsonInclude(value=Include.NON_NULL)
	@Schema(title="参数校验错误列表", example="name is empty", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
	private Map<String, List<String>> validationErrors;
	
	@Schema(title="业务模型对象", example="Any Object", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
	private T result;
	
	@JsonIgnore
	@Schema(title="响应类型（目前仅用于登录登出操作，1 - 登录 - 2：登出）", example="null", requiredMode=RequiredMode.NOT_REQUIRED, nullable=true)
	private Integer respType;

	public Response()
	{

	}

	public Response(T result)
	{
		this.result = result;
	}

	public Response(String msg, int statusCode)
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

	public Response(String msg, int statusCode, int resultCode)
	{
		this.msg = msg;
		this.statusCode = statusCode;
		this.resultCode = resultCode;
	}
	
	public long calcCostTime(long beginTime)
	{
		costTime = System.currentTimeMillis() - beginTime;
		
		return costTime;
	}

}
