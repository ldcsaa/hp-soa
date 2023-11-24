package io.github.hpsocket.soa.starter.web.cloud.exception;

import feign.Response;
import feign.Response.Body;
import feign.codec.ErrorDecoder;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson2.JSON;

@Slf4j
public class CloudErrorDecoder implements ErrorDecoder
{

    @Override
    public Exception decode(String methodKey, Response response)
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
                    info = JSON.parseObject(str, CloudExceptionInfo.class);
                    String exp = info.getException();
                    
                    if(GeneralHelper.isStrEmpty(exp))
                        ex = new ServiceException(String.format("服务调用失败 (%d) - %s", info.getStatus(), info.getPath()), INNER_API_CALL_EXCEPTION);
                    else
                    {
                        Class<?> clazz = Class.forName(info.getException());
                        
                        if(info.getStatusCode() != null)
                        {
                            ex = (Exception)clazz
                                    .getDeclaredConstructor(String.class, Integer.class, Integer.class)
                                    .newInstance(info.getMessage(), info.getStatusCode(), info.getResultCode());
                        }
                        else
                        {
                            ex = (Exception)clazz
                                    .getDeclaredConstructor(String.class)
                                    .newInstance(info.getMessage());
                        }
                    }
            
                }        
            }
            catch(Exception e)
            {
                ex = new ServiceException((info != null) ? info.getMessage() : "未知异常", INNER_API_CALL_EXCEPTION);
            }
        }
        
        log.error(ex.getMessage());
        
        return ex;
    }

}
