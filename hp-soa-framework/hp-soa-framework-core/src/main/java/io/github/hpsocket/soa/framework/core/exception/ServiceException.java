package io.github.hpsocket.soa.framework.core.exception;

import org.slf4j.Logger;

/** <b>HP-SOA 统一异常</b> */
@SuppressWarnings("serial")
public class ServiceException extends RuntimeException
{
    /** 成功 */
    public static final int OK                      = 0;
    /** 已接受 */
    public static final int ACCEPTED                = 202;
    /** 空内容 */
    public static final int NO_CONTENT              = 204;
    /** 部分成功 */
    public static final int PARTIAL_OK              = 206;
    /** 非法请求 */
    public static final int BAD_REQUEST             = 400;
    /** 目标不存在 */
    public static final int NOT_EXIST               = 404;
    /** 参数校验失败 */
    public static final int PARAM_VERIFY_ERROR      = 409;
    /** 服务器内部错误 */
    public static final int GENERAL_ERROR           = 500;
    /** 参数验证错误 */
    public static final int PARAM_VALIDATION_ERROR  = 501;
    /** 接口未实现 */
    public static final int NOT_IMPLEMENTED         = 502;
    /** 频次超限 */
    public static final int FREQUENCY_LIMIT_ERROR   = 503;
    /** 拒绝访问 */
    public static final int FORBID_ERROR            = 504;
    /** 请求参数非法 */
    public static final int PARAMS_ERROR            = 505;
    /** 重复请求 */
    public static final int REPEATED_REQ_ERROR      = 506;
    /** 访问被限流 */
    public static final int TRAFFIC_LIMIT_ERROR     = 507;
    /** 接口不支持 */
    public static final int NOT_SUPPORTED           = 508;
    /** 禁止更新 */
    public static final int FORBID_UPDATE_ERROR     = 509;
    /** 拒绝写入 */
    public static final int READ_ONLY_ERROR         = 510;
    /** 调用超时 */
    public static final int TIMEOUT_ERROR           = 511;
    /** 应用程序编号验证错误 */
    public static final int APPCODE_CHECK_ERROR     = 601;
    /** 应用程序编号不存在 */
    public static final int APPCODE_NOT_EXIST_ERROR = 602;
    /** 应用程序编号已存在 */
    public static final int APPCODE_EXIST_ERROR     = 603;
    /** 用户认证错误 */
    public static final int AUTHEN_ERROR            = 701;
    /** 授权验证错误 */
    public static final int AUTHOR_ERROR            = 702;
    /** 网络错误 */
    public static final int NETWORK_ERROR           = 801;
    /** 外部服务调用失败 */
    public static final int OUTER_API_CALL_FAIL     = 802;
    /** 内部服务调用失败 */
    public static final int INNER_API_CALL_FAIL     = 803;
    /** 签名验证失败 */
    public static final int SIGN_VERIFY_ERROR       = 901;
    /** 登录已失效 */
    public static final int LOGIN_INVALID           = 904;
    /** 未登录 */
    public static final int NOT_LOGGED_IN           = 907;
    
    public static final ServiceException BAD_REQUEST_EXCEPTION      = new ServiceException("非法请求", BAD_REQUEST);
    public static final ServiceException NOT_EXIST_EXCEPTION        = new ServiceException("目标不存在", NOT_EXIST);
    public static final ServiceException PARAM_VERIFY_EXCEPTION     = new ServiceException("参数校验失败", PARAM_VERIFY_ERROR);
    public static final ServiceException GENERAL_EXCEPTION          = new ServiceException("服务器内部错误", GENERAL_ERROR);
    public static final ServiceException PARAM_VALIDATION_EXCEPTION = new ServiceException("参数验证错误", PARAM_VALIDATION_ERROR);
    public static final ServiceException NOT_IMPLEMENTED_EXCEPTION  = new ServiceException("接口未实现", NOT_IMPLEMENTED);
    public static final ServiceException NOT_SUPPORTED_EXCEPTION    = new ServiceException("接口不支持", NOT_SUPPORTED);
    public static final ServiceException FORBID_SERVICE_EXCEPTION   = new ServiceException("禁止更新", FORBID_UPDATE_ERROR);
    public static final ServiceException APPCODE_CHECK_EXCEPTION    = new ServiceException("应用程序编号验证错误", APPCODE_CHECK_ERROR);
    public static final ServiceException APPCODE_NOT_EXIST_EXCEPTION= new ServiceException("应用程序编号不存在", APPCODE_NOT_EXIST_ERROR);
    public static final ServiceException APPCODE_EXIST_EXCEPTION    = new ServiceException("应用程序编号已存在", APPCODE_EXIST_ERROR);
    public static final ServiceException AUTHEN_EXCEPTION           = new ServiceException("用户认证失败", AUTHEN_ERROR);
    public static final ServiceException AUTHOR_EXCEPTION           = new ServiceException("授权验证失败", AUTHOR_ERROR);
    public static final ServiceException NETWORK_EXCEPTION          = new ServiceException("网络错误", NETWORK_ERROR);
    public static final ServiceException PARAMS_EXCEPTION           = new ServiceException("请求参数非法", PARAMS_ERROR);
    public static final ServiceException REPEATED_REQ_EXCEPTION     = new ServiceException("请勿重复请求", REPEATED_REQ_ERROR);
    public static final ServiceException TIMEOUT_EXCEPTION          = new ServiceException("调用超时", TIMEOUT_ERROR);
    public static final ServiceException OUTER_API_CALL_EXCEPTION   = new ServiceException("外部服务调用失败", OUTER_API_CALL_FAIL);
    public static final ServiceException INNER_API_CALL_EXCEPTION   = new ServiceException("内部服务调用失败", INNER_API_CALL_FAIL);
    public static final ServiceException SIGN_VERIFY_EXCEPTION      = new ServiceException("签名验证失败", SIGN_VERIFY_ERROR);

    public static final ServiceException FREQUENCY_LIMIT_EXCEPTION  = new UnimportantException("系统繁忙", FREQUENCY_LIMIT_ERROR);
    public static final ServiceException FORBID_EXCEPTION           = new UnimportantException("拒绝访问", FORBID_ERROR);
    public static final ServiceException TRAFFIC_LIMIT_EXCEPTION    = new UnimportantException("系统繁忙", TRAFFIC_LIMIT_ERROR);
    public static final ServiceException READ_ONLY_EXCEPTION        = new UnimportantException("拒绝写入", READ_ONLY_ERROR);
    public static final ServiceException LOGIN_INVALID_EXCEPTION    = new UnimportantException("登录已失效", LOGIN_INVALID);
    public static final ServiceException NOT_LOGGED_IN_EXCEPTION    = new UnimportantException("未登录", NOT_LOGGED_IN);
    
    /** 状态码：{@linkplain ServiceException#OK OK} - 成功，其它 - 失败 */
    private Integer statusCode;
    /** 结果码：用于服务内部监控、统计，不暴露到调用方，{@linkplain ServiceException#OK OK} - 成功，其它 - 失败 */
    private transient Integer resultCode;    
    
    public ServiceException()
    {
    }

    public ServiceException(String message)
    {
        this(message, GENERAL_ERROR);
    }

    public ServiceException(String message, Integer statusCode)
    {
        this(message, statusCode, statusCode);
    }

    public ServiceException(String message, Integer statusCode, Integer resultCode)
    {
        super(message);
        setStatusCode(statusCode);
        setResultCode(resultCode);
    }

    public ServiceException(String message, Throwable cause)
    {
        super(message, cause);
        if(cause instanceof ServiceException)
        {
            setStatusCode(((ServiceException)cause).getStatusCode());
            setResultCode(((ServiceException)cause).getResultCode());
        }
    }

    public ServiceException(String message, Integer statusCode, Throwable cause)
    {
        super(message, cause);
        setStatusCode(statusCode);
        setResultCode(statusCode);
    }

    public Integer getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(Integer resultCode)
    {
        this.resultCode = resultCode;
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode)
    {
        this.statusCode = statusCode;
    }

    public static final ServiceException wrapServiceException(Throwable e)
    {
        return wrapServiceException(GENERAL_EXCEPTION.getMessage(), GENERAL_EXCEPTION.statusCode, e);
    }

    public static final ServiceException wrapServiceException(ServiceException se, Throwable e)
    {
        return wrapServiceException(se.getMessage(), se.getStatusCode(), e);
    }

    public static final ServiceException wrapServiceException(String message, Integer statusCode, Throwable e)
    {
        if(!(e instanceof ServiceException))
            e = new ServiceException(message, statusCode, e);

        return (ServiceException)e;
    }

    public static final void throwServiceException(Throwable e)
    {
        throw wrapServiceException(e);
    }

    public static final void throwServiceException(Throwable e, String message)
    {
        throw wrapServiceException(message, GENERAL_ERROR, e);
    }

    public static final void throwServiceException(Throwable e, String format, Object... args)
    {
        String message = String.format(format, args);
        throw wrapServiceException(message, GENERAL_ERROR, e);
    }

    public static final void throwServiceException(String message, Integer statusCode)
    {
        throw new ServiceException(message, statusCode);
    }

    public static final void throwServiceException(String message, Integer statusCode, Throwable e)
    {
        throw new ServiceException(message, statusCode, e);
    }

    public static final void throwServiceException(ServiceException e, Object... args)
    {
        throwFormattedServiceException(e, e.getMessage(), args);
    }

    public static final void throwFormattedServiceException(Integer statusCode, String format, Object... args)
    {
        throw wrapFormattedServiceException(statusCode, format, args);
    }

    public static final ServiceException wrapFormattedServiceException(Integer statusCode, String format, Object... args)
    {
        String message = String.format(format, args);
        return new ServiceException(message, statusCode);
    }

    public static final void throwFormattedServiceException(ServiceException e, String format, Object... args)
    {
        throw wrapFormattedServiceException(e, format, args);
    }

    public static final ServiceException wrapFormattedServiceException(ServiceException e, String format, Object... args)
    {
        String message = String.format(format, args);
        return new ServiceException(message, e.getStatusCode(), e.getCause());
    }

    public static final void throwValidateException(String message)
    {
        throw wrapValidateException(message);
    }

    public static final ServiceException wrapValidateException(String message)
    {
        return new ServiceException(message, PARAM_VALIDATION_ERROR);
    }

    public static final void throwValidateException(String format, Object... args)
    {
        throwFormattedServiceException(PARAM_VALIDATION_EXCEPTION, format, args);
    }

    public static final ServiceException wrapValidateException(String format, Object... args)
    {
        return wrapFormattedServiceException(PARAM_VALIDATION_EXCEPTION, format, args);
    }

    public static void logException(Logger logger, Throwable e)
    {
        if(e instanceof ServiceException)
            logServiceException(logger, (ServiceException)e);
        else
            logException(logger, GENERAL_ERROR, e.getMessage(), e);
    }

    public static void logException(Logger logger, Integer code, String msg, Throwable e)
    {
        logServiceException(logger, new ServiceException(msg, code, e));
    }

    public static void logServiceException(Logger logger, ServiceException e)
    {
        logServiceException(logger, e.getMessage(), e, true);
    }

    public static void logServiceException(Logger logger, ServiceException e, boolean printWarnStackTrace)
    {
        logServiceException(logger, e.getMessage(), e, printWarnStackTrace);
    }

    public static void logServiceException(Logger logger, String msg, ServiceException e)
    {
        logServiceException(logger, msg, e, true);
    }

    public static void logServiceException(Logger logger, String msg, ServiceException e, boolean printWarnStackTrace)
    {
        final String FORMAT = "(SERVICE EXCEPTION - statusCode: {}, resultCode: {}) -> {}";
        final Integer statusCode = e.getStatusCode();
        final Integer resultCode = e.getResultCode();

        if(GENERAL_ERROR == statusCode)
            logger.error(FORMAT, statusCode, resultCode, msg, e);
        else
        {
            if(e instanceof UnimportantException)
                logger.info(FORMAT, statusCode, resultCode, msg);
            else
            {
                if(printWarnStackTrace)
                    logger.warn(FORMAT, statusCode, resultCode, msg, e);
                else
                    logger.warn(FORMAT, statusCode, resultCode, msg);
            }
        }
    }

}
