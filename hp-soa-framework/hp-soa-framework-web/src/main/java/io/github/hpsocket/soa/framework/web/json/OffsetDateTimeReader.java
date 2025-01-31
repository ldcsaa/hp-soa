package io.github.hpsocket.soa.framework.web.json;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.function.Function;

/** <b>FastJson {@linkplain OffsetDateTime} 解析器</b><br>
 * 用于设置 {@linkplain com.alibaba.fastjson2.annotation.JSONField#deserializeUsing() JSONField#deserializeUsing()} 注解属性
 */
public class OffsetDateTimeReader extends GenericDateTimeReader<OffsetDateTime>
{

    @Override
    protected Function<LocalDateTime, OffsetDateTime> getConverter()
    {
        return (dt) -> OffsetDateTime.of(dt, ZoneOffset.of(TimeZone.getDefault().getID()));
    }

}
