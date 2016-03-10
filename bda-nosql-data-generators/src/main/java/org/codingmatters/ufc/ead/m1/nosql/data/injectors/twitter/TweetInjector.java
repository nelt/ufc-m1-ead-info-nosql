package org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter;

import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;

/**
 * Created by vagrant on 2/24/16.
 */
public interface TweetInjector {
    String inject(Tweet tweet) throws InjectionException;
}
