package io.github.hpsocket.soa.starter.web.cloud.exception;

import feign.Response.Body;
import feign.codec.ErrorDecoder;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import java.lang.reflect.Constructor;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;

/** <b>Spring Cloud 异常解析器</b> */
@Slf4j
public class CloudErrorDecoder implements ErrorDecoder
{

    @Override
    public Exception decode(String methodKey, feign.Response response)
    {
        Body body = response.body();
        
        if(body == null)
            return INNER_API_CALL_EXCEPTION;
        
        CloudExceptionInfo info = null;
        String desc = INNER_API_CALL_EXCEPTION.getMessage();
        
        try
        {
            String str = IOUtils.toString(body.asReader(WebServerHelper.DEFAULT_CHARSET_OBJ));
            
            if(!str.startsWith("{"))
                return new ServiceException(GeneralHelper.isStrNotEmpty(str) ? str : desc, INNER_API_CALL_EXCEPTION);
            
            JSONObject json   = JSONObject.parse(str);
            String statusCode = json.getString("statusCode");

            if(GeneralHelper.isStrEmpty(statusCode))
            {
                info = json.to(CloudExceptionInfo.class);
                return new ServiceException(String.format("%s (status: %d) %s -> %s", desc, info.getStatus(), info.getError(), info.getPath()), INNER_API_CALL_EXCEPTION);
            }
            
            Response<CloudExceptionInfo> resp = json.to(new TypeReference<Response<CloudExceptionInfo>>() {});
            info = resp.getResult();
            
            if(info == null)
                return new ServiceException(String.format("%s (statusCode: %d) -> %s", desc, resp.getStatusCode(), resp.getMsg()), INNER_API_CALL_EXCEPTION);
            
            Class<?> clazz = null;
            
            try
            {
                clazz = Class.forName(info.getException());
            }
            catch(Exception e)
            {
            
            }
            
            if(clazz == null)
                return new ServiceException(String.format("%s: %s -> %s", desc, info.getException(), info.getMessage()), INNER_API_CALL_EXCEPTION);

            Constructor<?> cstor = null;
            
            try
            {
                cstor = clazz.getDeclaredConstructor(String.class);
            }
            catch(Exception e)
            {
                
            }
            
            if(cstor == null)
                return new ServiceException(String.format("%s: %s -> %s", desc, info.getException(), info.getMessage()), INNER_API_CALL_EXCEPTION);

            Exception ex = (Exception)cstor.newInstance(info.getMessage());
            
            if(ex instanceof ServiceException se)
            {
                se.setStatusCode(info.getStatusCode());
                se.setResultCode(info.getResultCode());
            }
            
            return ex;
        }
        catch(Exception e)
        {
            log.error(e.getMessage(), e);
            
            String msg = desc;
            
            if(info != null)
            {
                if(GeneralHelper.isStrNotEmpty(info.getMessage()))
                    msg = info.getMessage();
                else if(GeneralHelper.isStrNotEmpty(info.getError()))
                    msg = info.getError();
            }
            
            return new ServiceException(msg, INNER_API_CALL_EXCEPTION);
        }
    }

}
