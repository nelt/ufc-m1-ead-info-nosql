package org.codingmatters.ufc.ead.m1.nosql.data.service.tweet;

/**
 * Created by vagrant on 3/3/16.
 */
public class HtagFacet {
    private final String htag;
    private final long docCount;

    public HtagFacet(String htag, long docCount) {
        this.htag = htag;
        this.docCount = docCount;
    }

    public String getHtag() {
        return htag;
    }

    public long getDocCount() {
        return docCount;
    }
}
