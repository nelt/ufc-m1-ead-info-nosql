package org.codingmatters.ufc.ead.m1.nosql.twitter;

import twitter4j.Status;

/**
 * Created by ubuntu on 09/01/16.
 */
public interface StatusConsumer {
    void onStatus(Status status);
}
