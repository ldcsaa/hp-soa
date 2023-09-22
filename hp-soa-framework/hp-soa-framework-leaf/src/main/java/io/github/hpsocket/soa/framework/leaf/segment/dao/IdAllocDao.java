
package io.github.hpsocket.soa.framework.leaf.segment.dao;

import java.util.List;

import io.github.hpsocket.soa.framework.leaf.segment.model.LeafAlloc;

public interface IdAllocDao
{
	List<LeafAlloc> getAllLeafAllocs();

	LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

	LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

	List<String> getAllTags();
}
