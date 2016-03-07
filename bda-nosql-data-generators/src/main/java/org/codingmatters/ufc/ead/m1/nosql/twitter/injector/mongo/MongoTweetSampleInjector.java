package org.codingmatters.ufc.ead.m1.nosql.twitter.injector.mongo;

import com.mongodb.MongoClient;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.TweetSampleInjector;

import java.io.IOException;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 3/7/16.
 */
public class MongoTweetSampleInjector {
    public static void main(String[] args) {
        MongoClient client = new MongoClient(resolver().resolve("mongo"), 27017);
        try {
            new TweetSampleInjector(args, new MongoTweetInjector(client.getDatabase("twitter")), 10).run();
        } catch (InterruptedException | IOException e) {
            throw  new RuntimeException("error running sample injector", e);
        } finally {
            client.close();
        }
    }
}
