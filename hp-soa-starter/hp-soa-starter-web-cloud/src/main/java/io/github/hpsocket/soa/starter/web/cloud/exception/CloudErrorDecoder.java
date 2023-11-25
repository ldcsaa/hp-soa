package io.github.hpsocket.soa.starter.web.cloud.exception;

import feign.Response.Body;
import feign.codec.ErrorDecoder;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import java.lang.reflect.Constructor;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

public class CloudErrorDecoder implements ErrorDecoder
{

    @Override
    public Exception decode(String methodKey, feign.Response response)
    {
        Exception ex = null;
        Body body    = response.body();
        
        if(body == null)
            ex = INNER_API_CALL_EXCEPTION;
        else
        {
            CloudExceptionInfo info = null;
            
            try
            {
                String str = IOUtils.toString(body.asReader(WebServerHelper.DEFAULT_CHARSET_OBJ));
                
                if(!str.startsWith("{"))
                    ex = new ServiceException(GeneralHelper.isStrNotEmpty(str) ? str : "未知异常", INNER_API_CALL_EXCEPTION);
                else
                {
                    Response<CloudExceptionInfo> resp = JSON.parseObject(str, new TypeReference<Response<CloudExceptionInfo>>() {});
                    Integer statusCode = resp.getStatusCode();
                    
                    if(statusCode == null)
                    {
                        info = JSON.parseObject(str, CloudExceptionInfo.class);
                        ex = new ServiceException(String.format("服务调用失败: (%d) %s -> %s", info.getStatus(), info.getError(), info.getPath()), INNER_API_CALL_EXCEPTION);
                    }
                    else
                    {
                        info = resp.getResult();
                        Class<?> clazz = Class.forName(info.getException());
                        Constructor<?> cstor = null;
                        
                        try
                        {
                            cstor = clazz.getDeclaredConstructor(String.class);
                        }
                        catch(Exception e)
                        {
                            
                        }
                        
                        if(cstor == null)
                        {
                            ex = new ServiceException(String.format("服务调用失败: %s -> %s", info.getException(), info.getMessage()), INNER_API_CALL_EXCEPTION);
                        }
                        else
                        {
                            ex = (Exception)cstor.newInstance(info.getMessage());
                            
                            if(ex instanceof ServiceException se)
                            {
                                se.setStatusCode(info.getStatusCode());
                                se.setResultCode(info.getResultCode());
                            }                            
                        }
                    }
                }        
            }
            catch(Exception e)
            {
                String msg = "未知异常";
                
                if(info != null)
                {
                    if(GeneralHelper.isStrNotEmpty(info.getMessage()))
                        msg = info.getMessage();
                    else if(GeneralHelper.isStrNotEmpty(info.getError()))
                        msg = info.getError();
                }
                
                ex = new ServiceException(msg, INNER_API_CALL_EXCEPTION);
            }
        }
        
        return ex;
    }

}
