package io.github.hpsocket.soa.starter.kafka.util;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

public class KafkaHelper
{
    public static final Headers addHeader(Headers headers, String key, Object value)
    {
        return headers.add(key, value != null ? value.toString().getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ) : null);
    }
    
    public static final String getHeaderValue(Headers headers, String key)
    {
        Header header = headers.lastHeader(key);
        return (header != null) ? new String(header.value(), WebServerHelper.DEFAULT_CHARSET_OBJ) : null;
    }
    
}
