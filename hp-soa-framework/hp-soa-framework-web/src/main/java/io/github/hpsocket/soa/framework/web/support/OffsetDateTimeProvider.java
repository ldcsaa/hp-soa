package io.github.hpsocket.soa.framework.web.support;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.data.auditing.DateTimeProvider;

/** <b>{@linkplain OffsetDateTime} 日期时间提供者<b> */
public enum OffsetDateTimeProvider implements DateTimeProvider
{
    INSTANCE;

    @Override
    public Optional<TemporalAccessor> getNow()
    {
        return Optional.of(OffsetDateTime.now());
    }

}
