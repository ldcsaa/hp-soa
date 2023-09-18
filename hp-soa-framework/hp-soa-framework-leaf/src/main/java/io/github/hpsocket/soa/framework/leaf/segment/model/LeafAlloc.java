package io.github.hpsocket.soa.framework.leaf.segment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeafAlloc {
    private String key;
    private long maxId;
    private int step;
    private String updateTime;

}
