package io.github.hpsocket.soa.starter.skywalking.async;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import org.apache.skywalking.apm.toolkit.trace.ConsumerWrapper;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import java.util.function.Consumer;

/** <b>{@linkplain Consumer} 包装类（注入 {@linkplain org.slf4j.MDC MDC} 和 traceId 调用链跟踪信息）</b> */
@TraceCrossThread
public class TracingConsumerWrapper<V> extends ConsumerWrapper<V>
{
    private final MdcAttr mdcAttr;
    
    public TracingConsumerWrapper(Consumer<V> c)
    {
        this(c, MdcAttr.fromMdc());
    }
    
    public TracingConsumerWrapper(Consumer<V> c, MdcAttr mdcAttr)
    {
        super(c);
        
        this.mdcAttr = mdcAttr;
    }
    
    @Override
    public void accept(V v)
    {        
        try
        {
            mdcAttr.putMdc();
            
            super.accept(v);
        }
        finally
        {
            mdcAttr.removeMdc();
        }
    }

    public static <V> TracingConsumerWrapper<V> of(Consumer<V> c)
    {
        return new TracingConsumerWrapper<>(c);
    }

}
