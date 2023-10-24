package io.github.hpsocket.soa.framework.web.support;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

/** <b>{@linkplain String} -&gt; {@linkplain byte[]} 转换器<b> */
@ConfigurationPropertiesBinding
public class StringByteArrayConverter implements Converter<String, byte[]>
{

    @Override
    public byte[] convert(String source)
    {
        if(source == null)
            return null;
        
        return source.getBytes(Charset.forName("UTF-8"));
    }

}
