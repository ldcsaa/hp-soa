package io.github.hpsocket.soa.framework.web.event;

import lombok.Getter;

import java.time.Clock;

/**
 * @author Bruce
 * @date 2026/7/7 14:03
 */
@Getter
public class SeriousExceptionEvent extends MdcApplicationEvent
{
    private Throwable error;

    public SeriousExceptionEvent(Throwable error)
    {
        super(error);
    }

    public SeriousExceptionEvent(Throwable error, Clock clock)
    {
        super(error, clock);
    }

    public Throwable getSource()
    {
        return error;
    }
}
