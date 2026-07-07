package io.github.hpsocket.soa.framework.web.event;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author Bruce
 * @date 2026/7/7 05:33
 */
@Getter
public class MdcApplicationEvent extends ApplicationEvent
{
    private MdcAttr mdcAttr;

    public MdcApplicationEvent(Object source)
    {
        super(source, Clock.systemDefaultZone());
    }

    public MdcApplicationEvent(Object source, Clock clock)
    {
        super(source, clock);
        this.mdcAttr = MdcAttr.fromMdc();
    }
}
