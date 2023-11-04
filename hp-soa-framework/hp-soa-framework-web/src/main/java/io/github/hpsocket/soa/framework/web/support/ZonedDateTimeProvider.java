package io.github.hpsocket.soa.framework.web.support;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.data.auditing.DateTimeProvider;

/** <b>{@linkplain ZonedDateTime} 日期时间提供者<b> */
public class ZonedDateTimeProvider implements DateTimeProvider
{

    @Override
    public Optional<TemporalAccessor> getNow()
    {
        return Optional.of(ZonedDateTime.now());
    }

}
