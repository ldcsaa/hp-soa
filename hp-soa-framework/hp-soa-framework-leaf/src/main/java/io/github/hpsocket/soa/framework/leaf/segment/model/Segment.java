
package io.github.hpsocket.soa.framework.leaf.segment.model;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Segment
{
	private AtomicLong value = new AtomicLong(0);
	private volatile long max;
	private volatile int step;
	private SegmentBuffer buffer;

	public Segment(SegmentBuffer buffer)
	{
		this.buffer = buffer;
	}

	public long getIdle()
	{
		return this.getMax() - getValue().get();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Segment(");
		sb.append("value:");
		sb.append(value);
		sb.append(",max:");
		sb.append(max);
		sb.append(",step:");
		sb.append(step);
		sb.append(")");
		
		return sb.toString();
	}
}
