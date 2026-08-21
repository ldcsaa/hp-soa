package io.github.hpsocket.soa.framework.web.converter;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/** 日期时间 &lt;-&gt; 毫秒 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DateTimeToMillisecondMapper
{

    /**
     * Long (时间戳) -> LocalDateTime
     */
    default LocalDateTime longToLocalDateTime(Long timestamp)
    {
        if(timestamp == null)
            return null;

        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime -> Long (时间戳)
     */
    default Long localDateTimeToLong(LocalDateTime localDateTime)
    {
        if(localDateTime == null)
            return null;

        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Long (时间戳) -> LocalDate
     */
    default LocalDate longToLocalDate(Long timestamp)
    {
        if(timestamp == null)
            return null;

        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * LocalDate -> Long (时间戳)
     */
    default Long localDateToLong(LocalDate localDate)
    {
        if(localDate == null)
            return null;

        return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Long (时间戳) -> Date
     */
    default Date longToDate(Long timestamp)
    {
        if(timestamp == null)
            return null;

        return new Date(timestamp);
    }

    /**
     * Date -> Long (时间戳)
     */
    default Long dateToLong(Date date)
    {
        if(date == null)
            return null;

        return date.getTime();
    }

}
