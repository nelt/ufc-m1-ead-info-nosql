package org.codingmatters.ufc.ead.m1.nosql.twitter.injector.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.InjectionException;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.TweetInjector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vagrant on 2/24/16.
 */
public class MongoTweetInjector implements TweetInjector {

    private final MongoDatabase db;

    public MongoTweetInjector(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public String inject(Tweet tweet) throws InjectionException {
        this.db.getCollection("tweets").insertOne(
                new Document("text", tweet.getText())
                .append("createdAt", tweet.getCreatedAt())
                .append("htags", tweet.getHtags())
                .append("user",
                        new Document("name", tweet.getUser().getName())
                        .append("followersCount", tweet.getUser().getFollowersCount())
                )

        );
        return null;
    }
}
