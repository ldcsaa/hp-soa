package io.github.hpsocket.soa.framework.web.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

/** <b>应用程序只读事件</b><br>
 * 触发时机：
 * <ol>
 * <li>应用程序启动时，跟随 {@linkplain org.springframework.context.event.ContextRefreshedEvent ContextRefreshedEvent} 事件之后 （此时 {@linkplain ReadOnlyEvent#isInitial()} 为 true）</li>
 * <li>动态修改应用程序的 <i>${hp.soa.web.app.read-only}</i> 配置时，（此时 {@linkplain ReadOnlyEvent#isInitial()} 为 faise）</li>
 * </ol>
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class ReadOnlyEvent extends ApplicationEvent
{
	/** 是否只读 */
	private boolean readOnly;
	/** 是否初始触发 */
	private boolean initial;
	
	public ReadOnlyEvent(Object source, boolean readOnly)
	{
		this(source, readOnly, false);
	}

	public ReadOnlyEvent(Object source, boolean readOnly, boolean initial)
	{
		super(source);
		this.readOnly = readOnly;
		this.initial  = initial;
	}

}
