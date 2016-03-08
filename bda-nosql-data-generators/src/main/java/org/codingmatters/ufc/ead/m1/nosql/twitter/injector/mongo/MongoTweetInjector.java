package org.codingmatters.ufc.ead.m1.nosql.twitter.injector.mongo;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.InjectionException;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.TweetInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vagrant on 2/24/16.
 */
public class MongoTweetInjector implements TweetInjector {

    static private final Logger log = LoggerFactory.getLogger(MongoTweetInjector.class);

    private final MongoDatabase db;

    public MongoTweetInjector(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public String inject(Tweet tweet) throws InjectionException {
        Document dbTweet = new Document("text", tweet.getText())
                .append("createdAt", tweet.getCreatedAt())
                .append("htags", tweet.getHtags())
                .append("user",
                        new Document("name", tweet.getUser().getName())
                                .append("followersCount", tweet.getUser().getFollowersCount())
                );
        this.db.getCollection("tweets").insertOne(dbTweet);

        ObjectId id = (ObjectId) dbTweet.get("_id");

        for (String htag : tweet.getHtags()) {
            this.db.getCollection(htag + "_tweets").insertOne(
                    new Document("tweet_id", id)
                            .append("createdAt", tweet.getCreatedAt())
            );
        }


        return id.toString();
    }
}
