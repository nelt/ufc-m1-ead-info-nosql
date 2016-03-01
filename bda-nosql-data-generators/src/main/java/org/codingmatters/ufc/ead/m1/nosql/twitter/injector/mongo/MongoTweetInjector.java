package org.codingmatters.ufc.ead.m1.nosql.twitter.injector.mongo;

import com.mongodb.MongoClient;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.InjectionException;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.TweetInjector;

/**
 * Created by vagrant on 2/24/16.
 */
public class MongoTweetInjector implements TweetInjector {
    private final MongoClient client;

    public MongoTweetInjector(MongoClient client) {
        this.client = client;
    }

    @Override
    public String inject(Tweet tweet) throws InjectionException {
        return null;
    }
}
