package org.codingmatters.ufc.ead.m1.nosql.twitter.injector;

import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;

/**
 * Created by vagrant on 2/24/16.
 */
public interface TweetInjector {
    String inject(Tweet tweet) throws InjectionException;
}
