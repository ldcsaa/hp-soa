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
	
	public ReadOnlyEvent(Object source, boolean readOnly)
	{
		super(source);
		this.readOnly = readOnly;
	}

}
