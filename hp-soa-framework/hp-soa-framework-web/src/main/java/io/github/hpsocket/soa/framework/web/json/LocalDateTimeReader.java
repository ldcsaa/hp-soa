package io.github.hpsocket.soa.framework.web.json;

import java.time.LocalDateTime;
import java.util.function.Function;

/** <b>FastJson {@linkplain LocalDateTime} 解析器</b><br>
 * 用于设置 {@linkplain com.alibaba.fastjson2.annotation.JSONField#deserializeUsing() JSONField#deserializeUsing()} 注解属性
 */
public class LocalDateTimeReader extends GenericDateTimeReader<LocalDateTime>
{

    @Override
    protected Function<LocalDateTime, LocalDateTime> getConverter()
    {
        return Function.identity();
    }

}
