package io.github.hpsocket.soa.framework.web.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class ReadOnlyEvent extends ApplicationEvent
{
	private boolean readOnly;
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
