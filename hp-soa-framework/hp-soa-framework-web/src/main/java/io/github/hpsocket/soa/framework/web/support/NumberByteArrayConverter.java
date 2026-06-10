package io.github.hpsocket.soa.framework.web.support;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

import java.nio.charset.Charset;

/** <b>{@linkplain Number} -&gt; {@linkplain byte[]} 转换器<b> */
@ConfigurationPropertiesBinding
public class NumberByteArrayConverter implements Converter<Number, byte[]>
{

    @Override
    public byte[] convert(Number source)
    {
        if(source == null)
            return null;
        
        return source.toString().getBytes(Charset.forName("UTF-8"));
    }

}
