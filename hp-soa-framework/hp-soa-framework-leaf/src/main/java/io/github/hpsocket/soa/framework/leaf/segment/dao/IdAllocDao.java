
package io.github.hpsocket.soa.framework.leaf.segment.dao;

import io.github.hpsocket.soa.framework.leaf.segment.model.LeafAlloc;

import java.util.List;

public interface IdAllocDao
{
    List<LeafAlloc> getAllLeafAllocs();

    LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

    List<String> getAllTags();
}
