package com.nagare.common.counter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** counters: sinh ma tuan tu BK-2026-0001, TR-2026-0001 bang findAndModify nguyen tu. */
@Document(collection = "counters")
public class Counter {

    @Id
    private String id; // vi du "BK-2026"

    private long seq;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getSeq() { return seq; }
    public void setSeq(long seq) { this.seq = seq; }
}
