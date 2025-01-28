package io.github.hpsocket.soa.framework.web.json;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.function.Function;

/** <b>FastJson {@linkplain ZonedDateTime} 解析器</b><br>
 * 用于设置 {@linkplain com.alibaba.fastjson2.annotation.JSONField#deserializeUsing() JSONField#deserializeUsing()} 注解属性
 */
public class ZonedDateTimeReader extends GenericDateTimeReader<ZonedDateTime>
{

    @Override
    protected Function<LocalDateTime, ZonedDateTime> getConverter()
    {
        return (dt) -> ZonedDateTime.of(dt, ZoneOffset.of(TimeZone.getDefault().getID()));
    }

}
