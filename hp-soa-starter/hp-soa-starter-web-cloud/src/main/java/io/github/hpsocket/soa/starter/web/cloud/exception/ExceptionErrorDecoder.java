package io.github.hpsocket.soa.starter.web.cloud.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import groovy.util.logging.Slf4j;

@Slf4j
public class ExceptionErrorDecoder implements ErrorDecoder
{

    @Override
    public Exception decode(String methodKey, Response response)
    {
        try {
            if (response.body() != null) {
                
                String body = Util.toString(response.body().asReader());
                log.error(body);
                ExceptionInfo exceptionInfo = JSON.parseObject(body, new TypeReference<ExceptionInfo>() {
                    });
                Class clazz = Class.forName(exceptionInfo.getException());
                return (Exception) clazz.getDeclaredConstructor(String.class)
                    .newInstance(exceptionInfo.getMessage());
            }
        } catch (Exception var4) {
            log.error(var4.getMessage());
            return new InternalException(var4.getMessage());
        }
        return new InternalException("系统异常,请联系管理员");
    }

}
