package io.github.hpsocket.soa.starter.rocketmq.consumer.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import io.github.hpsocket.soa.framework.web.event.ReadOnlyEvent;

/** <b>{@linkplain ReadOnlyEvent} 应用程序事件处理器</b><br>
 * 当应用程序为只读时，关闭所有消息监听器，不接收任何消息<p>
 * <i>注：目前还没有实现</i>
 */
public class RocketmqReadOnlyEventListener implements ApplicationListener<ReadOnlyEvent>, Ordered
{
    @Override
    public void onApplicationEvent(ReadOnlyEvent event)
    {
        
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
