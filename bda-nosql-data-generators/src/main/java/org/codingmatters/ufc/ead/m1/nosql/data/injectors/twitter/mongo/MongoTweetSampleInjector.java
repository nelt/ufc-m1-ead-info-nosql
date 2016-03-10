package org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.mongo;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.TweetSampleInjector;

import java.io.IOException;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 3/7/16.
 */
public class MongoTweetSampleInjector {
    public static void main(String[] args) {
        MongoClient client = new MongoClient(resolver().resolve("mongo"), 27017);
        try {
            client.getDatabase("twitter").getCollection("tweets").createIndex(new Document("text", "text"));
            new TweetSampleInjector(args, new MongoTweetInjector(client.getDatabase("twitter")), 10).run();
        } catch (InterruptedException | IOException e) {
            throw  new RuntimeException("error running sample injector", e);
        } finally {
            client.close();
        }
    }
}
