
package org.hpsocket.soa.framework.web.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hpsocket.soa.framework.core.exception.ServiceException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import static org.hpsocket.soa.framework.core.exception.ServiceException.*;

@Getter
@Setter
@SuppressWarnings("serial")
@ApiModel(description="接口方法返回值对象")
public class Response<T> implements Serializable
{
	public static final String MSG_OK		= "ok";	
	public static final Integer RT_LOGIN	= 1;
	public static final Integer RT_LOGOUT	= 2;

	@ApiModelProperty(value="状态码", example="200", required=true, allowEmptyValue=false)
	private int statusCode = OK;
	@JsonIgnore
	@ApiModelProperty(value="业务处理代码", example="1000", required=false, allowEmptyValue=true)
	private transient int resultCode = OK;
	@ApiModelProperty(value="状态描述", example=MSG_OK, required=false, allowEmptyValue=true)
	private String msg = MSG_OK;
	@ApiModelProperty(value="耗时（毫秒）", example="456", required=false, allowEmptyValue=true)
	private long costTime;
	@ApiModelProperty(value="参数校验错误列表", example="name is empty", required=false, allowEmptyValue=true)
	@JsonInclude(Include.NON_NULL)
	private Map<String, List<String>> validationErrors;
	@ApiModelProperty(value="业务模型对象", example="Any Object", required=false, allowEmptyValue=true)
	private T result;
	
	@JsonIgnore
	@ApiModelProperty(value="响应类型（目前仅用于登录登出操作，1 - 登录 - 2：登出）", example="null", required=false, allowEmptyValue=true)
	private transient int respType;

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
